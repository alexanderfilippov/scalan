/**
 * User: Alexander Slesarenko
 * Date: 11/16/13
 */
package scalan.meta

object Extensions {
  implicit class IterableExtensions[A](val it: Iterable[A]) extends AnyVal
  {
    def opt(show: Iterable[A] => String = _.mkString, default: String = ""): String = 
      if (it.isEmpty) default else show(it)

    def rep(show: A => String = _.toString, sep: String = ", "): String = it.map(show).mkString(sep)

    def enumTypes(show: Int => String) = (1 to it.size).map(show)
  }

  def all[A](a: A): String = a.toString

  implicit class OptionExtensions[A](val opt: Option[A]) extends AnyVal {
    def opt(show: A => String = _.toString, default: String = ""): String = opt match {
      case None => default
      case Some(a) => show(a)
    }
  }
  
  implicit class BooleanExtensions(val opt: Boolean) extends AnyVal {
    def opt(show: => String, default: => String = ""): String = if(opt) show else default
  }
  
  implicit class StringExtensions(val str: String) extends AnyVal {
    def stripAndTrim = str.stripMargin.stripPrefix("\n").stripLineEnd
  }
}

trait ScalanCodegen extends ScalanAst with ScalanParsers { ctx: EntityManagement =>

  class EntityFileGenerator(module: SEntityModuleDef) {
    import Extensions._
    
    abstract class TemplateData(val name: String, val tpeArgs: List[STpeArg]) {
      val tpeArgNames = tpeArgs.map(_.name)
      val tpeArgString = tpeArgNames.opt(tyArgs => s"[${tyArgs.rep(t => t)}]")
      val boundedTpeArgString = tpeArgNames.opt(tyArgs => s"[${tyArgs.rep(t =>s"$t:Elem")}]")
      def nameWithArgs = name + tpeArgString
    }
    
    class EntityTemplateData(name: String, tpeArgs: List[STpeArg]) extends TemplateData(name, tpeArgs)
    
    object EntityTemplateData {
      def apply(t: STraitDef) = new EntityTemplateData(t.name, t.tpeArgs)
    }
    
    class ConcreteClassTemplateData(name: String, val args: List[SClassArg], implArgs: List[SClassArg], tpeArgs: List[STpeArg], val baseType: STraitCall) extends TemplateData(name, tpeArgs) {
      val argNames = args.map(a => a.name)
      val argNamesAndTypes = args.map(a => s"${a.name}: ${a.tpe}")
      val argUnrepTypes = args.map(a => a.tpe match {
        case STraitCall("Rep", List(t)) => t
        case _ => sys.error(s"Invalid field $a. Fields of concrete classes should be of type Rep[T] for some T.")
      })
      val implicitArgs = implArgs.opt(args => s"(implicit ${args.rep(a => s"${a.name}: ${a.tpe}")})")
      val useImplicits = implArgs.opt(args => s"(${args.map(_.name).rep(a => a)})")
      val implicitSignature = implArgs.opt(args => s"(implicit ${args.rep(a => s"override val ${a.name}: ${a.tpe}")})")
    }
    
    object ConcreteClassTemplateData {
      def apply(c: SClassDef) = new ConcreteClassTemplateData(c.name, c.args, c.implicitArgs, c.tpeArgs, c.ancestors.head)
    }

    def dataType(ts: List[STpeExpr]): String = ts match {
      case Nil => "Unit"
      case t :: Nil => t.toString
      case t :: ts => s"($t, ${dataType(ts)})"
    }
    
    def pairify(fs: List[String]): String = fs match {
      case Nil => "unit"
      case f :: Nil => f
      case f :: fs => s"Pair($f, ${pairify(fs)})"
    }
    
    def zeroSExpr(t: STpeExpr): String = t match {
      case STpeInt => 0.toString
      case STpeBoolean => false.toString
      case STpeFloat => 0f.toString
      case STpeString => "\"\""
      case STraitCall(name, args) => s"element[$t].defaultRepValue"
      case STpeTuple(items) => pairify(items.map(zeroSExpr))
      case STpeFunc(domain, range) => s"""fun { (x: Rep[${domain}]) => ${zeroSExpr(range)} }"""
      case t => throw new IllegalArgumentException(s"Can't generate zero value for $t")
    }

    val templateData = EntityTemplateData(module.entityOps)
    val entityName = templateData.name
    val tyArgs = templateData.tpeArgNames
    val types = templateData.tpeArgString
    val typesWithElems = templateData.boundedTpeArgString
    
    def getTraitAbs = {
      val companionName = s"${entityName}Companion"
      val proxy =
        s"""
        |  // single proxy for each type family
        |  implicit def proxy$entityName${typesWithElems}(p: Rep[$entityName${types}]): $entityName$types =
        |    proxyOps[$entityName${types}](p)
        |""".stripAndTrim

      val familyElem = s"""  abstract class ${entityName}Elem[From,To](iso: Iso[From, To]) extends ViewElem[From, To]()(iso)""".stripAndTrim
      val defaultElem = s"""
        |  // implicit def default${entityName}Elem${typesWithElems}: Elem[$entityName${types}] = ???
        |""".stripAndTrim
      val companionElem = s"""
        |  trait ${companionName}Elem extends CompanionElem[${companionName}Abs]
        |  implicit lazy val ${companionName}Elem: ${companionName}Elem = new ${companionName}Elem {
        |    lazy val tag = typeTag[${companionName}Abs]
        |    lazy val defaultRep = Default.defaultVal($entityName)
        |  }
        |
        |  trait ${companionName}Abs extends ${companionName}
        |  def $entityName: Rep[${companionName}Abs]
        |  implicit def proxy$companionName(p: Rep[${companionName}]): ${companionName} = {
        |    proxyOps[${companionName}](p, true)
        |  }
        |""".stripAndTrim
        
      val isoZero = "defaultRepTo"
      val defaultVal = "Default.defaultVal"

      val defs = for { c <- module.concreteSClasses } yield {
        val className = c.name
        val templateData = ConcreteClassTemplateData(c)
        val types = templateData.tpeArgString
        val typesWithElems = templateData.boundedTpeArgString
        val fields = templateData.argNames
        val fieldsWithType = templateData.argNamesAndTypes
        val fieldTypes = templateData.argUnrepTypes
        val traitWithTypes = templateData.baseType
        val implicitArgs = templateData.implicitArgs
        val useImplicits = templateData.useImplicits
        s"""
        |  // elem for concrete class
        |  class ${className}Elem${types}(iso: Iso[${className}Data${types}, $className${types}]) extends ${entityName}Elem[${className}Data${types}, $className${types}](iso)
        |
        |  // state representation type
        |  type ${className}Data${types} = ${dataType(fieldTypes)}
        |
        |  // 3) Iso for concrete class
        |  class ${className}Iso${types}${implicitArgs}
        |    extends Iso[${className}Data${types}, $className${types}] {
        |    override def from(p: Rep[$className${types}]) =
        |      unmk${className}(p) match {
        |        case Some((${fields.opt(fields => fields.rep(), "unit")})) => ${pairify(fields)}
        |        case None => !!!
        |      }
        |    override def to(p: Rep[${dataType(fieldTypes)}]) = {
        |      val ${pairify(fields)} = p
        |      $className(${fields.rep()})
        |    }
        |    lazy val tag = {
        |${c.tpeArgs.rep(a => s"      implicit val tag${a.name} = element[${a.name}].tag", "\n")}
        |      typeTag[$className${types}]
        |    }
        |    lazy val ${isoZero} = $defaultVal[Rep[$className${types}]]($className(${fieldTypes.rep(zeroSExpr(_))}))
        |    lazy val eTo = new ${className}Elem${types}(this)
        |  }
        |  // 4) constructor and deconstructor
        |  trait ${className}CompanionAbs extends ${className}Companion {
        |${(fields.length != 1).opt(s"""
        |    def apply${types}(p: Rep[${className}Data${types}])${implicitArgs}: Rep[$className${types}] =
        |      iso$className${useImplicits}.to(p)""".stripAndTrim)}
        |    def apply${types}(${fieldsWithType.rep()})${implicitArgs}: Rep[$className${types}] =
        |      mk$className(${fields.rep()})
        |    def unapply${typesWithElems}(p: Rep[$className${types}]) = unmk$className(p)
        |  }
        |  def $className: Rep[${className}CompanionAbs]
        |  implicit def proxy${className}Companion(p: Rep[${className}CompanionAbs]): ${className}CompanionAbs = {
        |    proxyOps[${className}CompanionAbs](p, true)
        |  }
        |
        |  trait ${className}CompanionElem extends CompanionElem[${className}CompanionAbs]
        |  implicit lazy val ${className}CompanionElem: ${className}CompanionElem = new ${className}CompanionElem {
        |    lazy val tag = typeTag[${className}CompanionAbs]
        |    lazy val defaultRep = Default.defaultVal($className)
        |  }
        |
        |  implicit def proxy$className${typesWithElems}(p: Rep[$className${types}]): ${className}${types} = {
        |    proxyOps[${className}${types}](p)
        |  }
        |
        |  implicit class Extended$className${types}(p: Rep[$className${types}])${implicitArgs} {
        |    def toData: Rep[${className}Data${types}] = iso$className${useImplicits}.from(p)
        |  }
        |
        |  // 5) implicit resolution of Iso
        |  implicit def iso$className${types}${implicitArgs}: Iso[${className}Data${types}, $className${types}] =
        |    new ${className}Iso${types}
        |
        |  // 6) smart constructor and deconstructor
        |  def mk$className${types}(${fieldsWithType.rep()})${implicitArgs}: Rep[$className${types}]
        |  def unmk$className${typesWithElems}(p: Rep[$className${types}]): Option[(${fieldTypes.opt(fieldTypes => fieldTypes.rep(t => s"Rep[$t]"), "Rep[Unit]")})]
        |""".stripAndTrim
      }

      s"""
       |trait ${module.name}Abs extends ${module.name}
       |{ ${module.selfType.opt(t => s"self: ${t.tpe} =>")}
       |$proxy
       |
       |$familyElem
       |
       |$companionElem
       |
       |${defs.mkString("\n\n")}
       |}
       |""".stripAndTrim
    }

    def getSClassSeq(c: SClassDef) = {
      val className = c.name
      val templateData = ConcreteClassTemplateData(c)
      val types = templateData.tpeArgString
      val typesWithElems = templateData.boundedTpeArgString
      val fields = templateData.argNames
      val fieldsWithType = templateData.argNamesAndTypes
      val traitWithTypes = templateData.baseType
      val implicitArgs = templateData.implicitArgs
      val implicitSignature = templateData.implicitSignature
      val userTypeDefs =
        s"""
         |  case class Seq$className${types}
         |      (${fieldsWithType.rep(f => s"override val $f")})
         |      ${implicitSignature}
         |    extends $className${types}(${fields.rep()})${c.selfType.opt(t => s" with ${t.tpe}")} with UserTypeSeq[$traitWithTypes, $className${types}] {
         |    lazy val selfType = element[${className}${types}].asInstanceOf[Elem[$traitWithTypes]]
         |  }
         |  lazy val $className = new ${className}CompanionAbs with UserTypeSeq[${className}CompanionAbs, ${className}CompanionAbs] {
         |    lazy val selfType = element[${className}CompanionAbs]
         |  }""".stripAndTrim

      val constrDefs =
        s"""
         |  def mk$className${types}
         |      (${fieldsWithType.rep()})$implicitArgs =
         |      new Seq$className${types}(${fields.rep()})${c.selfType.opt(t => s" with ${t.tpe}")}
         |  def unmk$className${typesWithElems}(p: Rep[$className${types}]) =
         |    Some((${fields.rep(f => s"p.$f")}))
         |""".stripAndTrim

      s"""$userTypeDefs\n\n$constrDefs"""
    }

    def getSClassExp(c: SClassDef) = {
      val className = c.name
      val templateData = ConcreteClassTemplateData(c)
      val types = templateData.tpeArgString
      val typesWithElems = templateData.boundedTpeArgString
      val fields = templateData.argNames
      val fieldsWithType = templateData.argNamesAndTypes
      val fieldTypes = templateData.argUnrepTypes
      val traitWithTypes = templateData.baseType
      val implicitArgs = templateData.implicitArgs
      val implicitSignature = templateData.implicitSignature
      val userTypeNodeDefs =
        s"""
         |  case class Exp$className${types}
         |      (${fieldsWithType.rep(f => s"override val $f")})
         |      ${implicitSignature}
         |    extends $className${types}(${fields.rep()})${c.selfType.opt(t => s" with ${t.tpe}")} with UserTypeDef[$traitWithTypes, $className${types}] {
         |    lazy val selfType = element[$className${types}].asInstanceOf[Elem[$traitWithTypes]]
         |    override def mirror(t: Transformer) = Exp$className${types}(${fields.rep(f => s"t($f)")})
         |  }
         |
         |  lazy val $className: Rep[${className}CompanionAbs] = new ${className}CompanionAbs with UserTypeDef[${className}CompanionAbs, ${className}CompanionAbs] {
         |    lazy val selfType = element[${className}CompanionAbs]
         |    override def mirror(t: Transformer) = this
         |  }
         |""".stripAndTrim

      val constrDefs =
        s"""
         |  def mk$className${types}
         |    (${fieldsWithType.rep()})$implicitArgs =
         |    new Exp$className${types}(${fields.rep()})${c.selfType.opt(t => s" with ${t.tpe}")}
         |  def unmk$className${typesWithElems}(p: Rep[$className${types}]) =
         |    Some((${fields.rep(f => s"p.$f")}))
         |""".stripAndTrim

      s"""$userTypeNodeDefs\n\n$constrDefs"""
    }

    def getTraitSeq = {
      val e = module.entityOps
      val entityName = e.name
      val defs = for { c <- module.concreteSClasses } yield getSClassSeq(c)

      s"""
       |trait ${module.name}Seq extends ${module.name}Abs { self: ${config.seqContextTrait}${module.selfType.opt(t => s" with ${t.tpe}")} =>
       |  lazy val $entityName: Rep[${entityName}CompanionAbs] = new ${entityName}CompanionAbs with UserTypeSeq[${entityName}CompanionAbs, ${entityName}CompanionAbs] {
       |    lazy val selfType = element[${entityName}CompanionAbs]
       |  }
       |
       |${defs.mkString("\n\n")}
       |}
       |""".stripAndTrim
    }

    def getTraitExp = {
      val e = module.entityOps
      val entityName = e.name
      val td = EntityTemplateData(e)
      var counter = Map.empty[String, Int].withDefaultValue(0)

      def methodExtractor(m: SMethodDef) = {
        val traitElem = s"${entityName}Elem[_, _]"
        val explicitArgs = m.args.filter(!_.impFlag).flatMap(_.args)
        val typeVars = (e.tpeArgs ++ m.tpeArgs).map(_.name).toSet
        val returnType = {
          val receiverType = td.nameWithArgs
          val argTypes = explicitArgs.map(_.tpe)
          val receiverAndArgTypes = 
            if (argTypes.isEmpty)
              receiverType
            else
              s"($receiverType, ${argTypes.mkString(", ")})"
          s"Option[$receiverAndArgTypes${typeVars.opt(typeVars => s" forSome {${typeVars.map("type " + _).mkString("; ")}}")}]"
        }
        val explicitArgsStr = explicitArgs.rep(_.name, ", ")
        val overloadNum = counter(m.name)
        val suffix = if (overloadNum > 0) overloadNum.toString else ""
        counter += m.name -> (overloadNum + 1)

        // TODO we can use name-based extractor to improve performance when we switch to Scala 2.11
        // See http://hseeberger.github.io/blog/2013/10/04/name-based-extractors-in-scala-2-dot-11/
        
        // FIXME overloads and implicit arguments may not be handled correctly 
        s"""
        |  object ${entityName}_${m.name}$suffix {
        |    def unapply(d: Def[_]): $returnType = d match {
        |      case MethodCall(receiver, m, Seq($explicitArgsStr)) if m.getName == "${m.name}" && receiver.elem.isInstanceOf[$traitElem] =>
        |        Some(${if (explicitArgs.isEmpty) "receiver" else s"(receiver, $explicitArgsStr)"}).asInstanceOf[$returnType]
        |      case _ => None
        |    }
        |    def unapply(exp: Exp[_]): $returnType = exp match {
        |      case Def(d) => unapply(d)
        |      case _ => None
        |    }
        |  }
        |""".stripAndTrim
      }

      val defs = for { c <- module.concreteSClasses } yield getSClassExp(c)
      val methods = e.body.collect { case m: SMethodDef => m }
      val methodExtractors = e.body.collect { case m: SMethodDef => methodExtractor(m) }
      
      s"""
       |trait ${module.name}Exp extends ${module.name}Abs { self: ${config.stagedContextTrait}${module.selfType.opt(t => s" with ${t.tpe}")} =>
       |  lazy val $entityName: Rep[${entityName}CompanionAbs] = new ${entityName}CompanionAbs with UserTypeDef[${entityName}CompanionAbs, ${entityName}CompanionAbs] {
       |    lazy val selfType = element[${entityName}CompanionAbs]
       |    override def mirror(t: Transformer) = this
       |  }
       |
       |${defs.mkString("\n\n")}
       |
       |${methodExtractors.mkString("\n\n")}
       |}
       |""".stripAndTrim
    }

    def getFileHeader = {
      s"""
      |package ${module.packageName}
      |package impl
      |
      |${(module.imports ++ config.extraImports.map(SImportStat(_))).rep(i => s"import ${i.name}", "\n")}
      |""".stripAndTrim
    }

    def getImplFile: String = {
      val topLevel = List(
        getFileHeader,
        getTraitAbs,
        getTraitSeq,
        getTraitExp
      )
      topLevel.mkString("", "\n\n", "\n")
    }
  }
}
