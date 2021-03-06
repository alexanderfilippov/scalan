package scalan.graphs

import scalan.collections.CollectionsDsl
import scalan._
import scala.reflect.runtime.universe.{WeakTypeTag, weakTypeTag}
import scalan.meta.ScalanAst._

package impl {
// Abs -----------------------------------
trait FrontsAbs extends Fronts with scalan.Scalan {
  self: FrontsDsl =>

  // single proxy for each type family
  implicit def proxyFront(p: Rep[Front]): Front = {
    proxyOps[Front](p)(scala.reflect.classTag[Front])
  }

  // familyElem
  class FrontElem[To <: Front]
    extends EntityElem[To] {
    lazy val parent: Option[Elem[_]] = None
    lazy val entityDef: STraitOrClassDef = {
      val module = getModules("Fronts")
      module.entities.find(_.name == "Front").get
    }
    lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }
    override def isEntityType = true
    override lazy val tag = {
      weakTypeTag[Front].asInstanceOf[WeakTypeTag[To]]
    }
    override def convert(x: Rep[Reifiable[_]]) = {
      implicit val eTo: Elem[To] = this
      val conv = fun {x: Rep[Front] => convertFront(x) }
      tryConvert(element[Front], this, x, conv)
    }

    def convertFront(x : Rep[Front]): Rep[To] = {
      assert(x.selfType1 match { case _: FrontElem[_] => true; case _ => false })
      x.asRep[To]
    }
    override def getDefaultRep: Rep[To] = ???
  }

  implicit def frontElement: Elem[Front] =
    new FrontElem[Front]

  implicit case object FrontCompanionElem extends CompanionElem[FrontCompanionAbs] {
    lazy val tag = weakTypeTag[FrontCompanionAbs]
    protected def getDefaultRep = Front
  }

  abstract class FrontCompanionAbs extends CompanionBase[FrontCompanionAbs] with FrontCompanion {
    override def toString = "Front"
  }
  def Front: Rep[FrontCompanionAbs]
  implicit def proxyFrontCompanion(p: Rep[FrontCompanion]): FrontCompanion =
    proxyOps[FrontCompanion](p)

  // elem for concrete class
  class BaseFrontElem(val iso: Iso[BaseFrontData, BaseFront])
    extends FrontElem[BaseFront]
    with ConcreteElem[BaseFrontData, BaseFront] {
    override lazy val parent: Option[Elem[_]] = Some(frontElement)
    override lazy val entityDef = {
      val module = getModules("Fronts")
      module.concreteSClasses.find(_.name == "BaseFront").get
    }
    override lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }

    override def convertFront(x: Rep[Front]) = // Converter is not generated by meta
!!!("Cannot convert from Front to BaseFront: missing fields List(bits)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = {
      weakTypeTag[BaseFront]
    }
  }

  // state representation type
  type BaseFrontData = (CollectionOverArray[Int], BitSet)

  // 3) Iso for concrete class
  class BaseFrontIso
    extends Iso[BaseFrontData, BaseFront]()(pairElement(implicitly[Elem[CollectionOverArray[Int]]], implicitly[Elem[BitSet]])) {
    override def from(p: Rep[BaseFront]) =
      (p.set, p.bits)
    override def to(p: Rep[(CollectionOverArray[Int], BitSet)]) = {
      val Pair(set, bits) = p
      BaseFront(set, bits)
    }
    lazy val defaultRepTo: Rep[BaseFront] = BaseFront(element[CollectionOverArray[Int]].defaultRepValue, element[BitSet].defaultRepValue)
    lazy val eTo = new BaseFrontElem(this)
  }
  // 4) constructor and deconstructor
  abstract class BaseFrontCompanionAbs extends CompanionBase[BaseFrontCompanionAbs] with BaseFrontCompanion {
    override def toString = "BaseFront"
    def apply(p: Rep[BaseFrontData]): Rep[BaseFront] =
      isoBaseFront.to(p)
    def apply(set: Rep[CollectionOverArray[Int]], bits: Rep[BitSet]): Rep[BaseFront] =
      mkBaseFront(set, bits)
  }
  object BaseFrontMatcher {
    def unapply(p: Rep[Front]) = unmkBaseFront(p)
  }
  def BaseFront: Rep[BaseFrontCompanionAbs]
  implicit def proxyBaseFrontCompanion(p: Rep[BaseFrontCompanionAbs]): BaseFrontCompanionAbs = {
    proxyOps[BaseFrontCompanionAbs](p)
  }

  implicit case object BaseFrontCompanionElem extends CompanionElem[BaseFrontCompanionAbs] {
    lazy val tag = weakTypeTag[BaseFrontCompanionAbs]
    protected def getDefaultRep = BaseFront
  }

  implicit def proxyBaseFront(p: Rep[BaseFront]): BaseFront =
    proxyOps[BaseFront](p)

  implicit class ExtendedBaseFront(p: Rep[BaseFront]) {
    def toData: Rep[BaseFrontData] = isoBaseFront.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoBaseFront: Iso[BaseFrontData, BaseFront] =
    new BaseFrontIso

  // 6) smart constructor and deconstructor
  def mkBaseFront(set: Rep[CollectionOverArray[Int]], bits: Rep[BitSet]): Rep[BaseFront]
  def unmkBaseFront(p: Rep[Front]): Option[(Rep[CollectionOverArray[Int]], Rep[BitSet])]

  // elem for concrete class
  class ListFrontElem(val iso: Iso[ListFrontData, ListFront])
    extends FrontElem[ListFront]
    with ConcreteElem[ListFrontData, ListFront] {
    override lazy val parent: Option[Elem[_]] = Some(frontElement)
    override lazy val entityDef = {
      val module = getModules("Fronts")
      module.concreteSClasses.find(_.name == "ListFront").get
    }
    override lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }

    override def convertFront(x: Rep[Front]) = // Converter is not generated by meta
!!!("Cannot convert from Front to ListFront: missing fields List(bits)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = {
      weakTypeTag[ListFront]
    }
  }

  // state representation type
  type ListFrontData = (CollectionOverList[Int], BitSet)

  // 3) Iso for concrete class
  class ListFrontIso
    extends Iso[ListFrontData, ListFront]()(pairElement(implicitly[Elem[CollectionOverList[Int]]], implicitly[Elem[BitSet]])) {
    override def from(p: Rep[ListFront]) =
      (p.set, p.bits)
    override def to(p: Rep[(CollectionOverList[Int], BitSet)]) = {
      val Pair(set, bits) = p
      ListFront(set, bits)
    }
    lazy val defaultRepTo: Rep[ListFront] = ListFront(element[CollectionOverList[Int]].defaultRepValue, element[BitSet].defaultRepValue)
    lazy val eTo = new ListFrontElem(this)
  }
  // 4) constructor and deconstructor
  abstract class ListFrontCompanionAbs extends CompanionBase[ListFrontCompanionAbs] with ListFrontCompanion {
    override def toString = "ListFront"
    def apply(p: Rep[ListFrontData]): Rep[ListFront] =
      isoListFront.to(p)
    def apply(set: Rep[CollectionOverList[Int]], bits: Rep[BitSet]): Rep[ListFront] =
      mkListFront(set, bits)
  }
  object ListFrontMatcher {
    def unapply(p: Rep[Front]) = unmkListFront(p)
  }
  def ListFront: Rep[ListFrontCompanionAbs]
  implicit def proxyListFrontCompanion(p: Rep[ListFrontCompanionAbs]): ListFrontCompanionAbs = {
    proxyOps[ListFrontCompanionAbs](p)
  }

  implicit case object ListFrontCompanionElem extends CompanionElem[ListFrontCompanionAbs] {
    lazy val tag = weakTypeTag[ListFrontCompanionAbs]
    protected def getDefaultRep = ListFront
  }

  implicit def proxyListFront(p: Rep[ListFront]): ListFront =
    proxyOps[ListFront](p)

  implicit class ExtendedListFront(p: Rep[ListFront]) {
    def toData: Rep[ListFrontData] = isoListFront.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoListFront: Iso[ListFrontData, ListFront] =
    new ListFrontIso

  // 6) smart constructor and deconstructor
  def mkListFront(set: Rep[CollectionOverList[Int]], bits: Rep[BitSet]): Rep[ListFront]
  def unmkListFront(p: Rep[Front]): Option[(Rep[CollectionOverList[Int]], Rep[BitSet])]

  // elem for concrete class
  class CollectionFrontElem(val iso: Iso[CollectionFrontData, CollectionFront])
    extends FrontElem[CollectionFront]
    with ConcreteElem[CollectionFrontData, CollectionFront] {
    override lazy val parent: Option[Elem[_]] = Some(frontElement)
    override lazy val entityDef = {
      val module = getModules("Fronts")
      module.concreteSClasses.find(_.name == "CollectionFront").get
    }
    override lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }

    override def convertFront(x: Rep[Front]) = // Converter is not generated by meta
!!!("Cannot convert from Front to CollectionFront: missing fields List(bits)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = {
      weakTypeTag[CollectionFront]
    }
  }

  // state representation type
  type CollectionFrontData = (Collection[Int], BitSet)

  // 3) Iso for concrete class
  class CollectionFrontIso
    extends Iso[CollectionFrontData, CollectionFront]()(pairElement(implicitly[Elem[Collection[Int]]], implicitly[Elem[BitSet]])) {
    override def from(p: Rep[CollectionFront]) =
      (p.set, p.bits)
    override def to(p: Rep[(Collection[Int], BitSet)]) = {
      val Pair(set, bits) = p
      CollectionFront(set, bits)
    }
    lazy val defaultRepTo: Rep[CollectionFront] = CollectionFront(element[Collection[Int]].defaultRepValue, element[BitSet].defaultRepValue)
    lazy val eTo = new CollectionFrontElem(this)
  }
  // 4) constructor and deconstructor
  abstract class CollectionFrontCompanionAbs extends CompanionBase[CollectionFrontCompanionAbs] with CollectionFrontCompanion {
    override def toString = "CollectionFront"
    def apply(p: Rep[CollectionFrontData]): Rep[CollectionFront] =
      isoCollectionFront.to(p)
    def apply(set: Rep[Collection[Int]], bits: Rep[BitSet]): Rep[CollectionFront] =
      mkCollectionFront(set, bits)
  }
  object CollectionFrontMatcher {
    def unapply(p: Rep[Front]) = unmkCollectionFront(p)
  }
  def CollectionFront: Rep[CollectionFrontCompanionAbs]
  implicit def proxyCollectionFrontCompanion(p: Rep[CollectionFrontCompanionAbs]): CollectionFrontCompanionAbs = {
    proxyOps[CollectionFrontCompanionAbs](p)
  }

  implicit case object CollectionFrontCompanionElem extends CompanionElem[CollectionFrontCompanionAbs] {
    lazy val tag = weakTypeTag[CollectionFrontCompanionAbs]
    protected def getDefaultRep = CollectionFront
  }

  implicit def proxyCollectionFront(p: Rep[CollectionFront]): CollectionFront =
    proxyOps[CollectionFront](p)

  implicit class ExtendedCollectionFront(p: Rep[CollectionFront]) {
    def toData: Rep[CollectionFrontData] = isoCollectionFront.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoCollectionFront: Iso[CollectionFrontData, CollectionFront] =
    new CollectionFrontIso

  // 6) smart constructor and deconstructor
  def mkCollectionFront(set: Rep[Collection[Int]], bits: Rep[BitSet]): Rep[CollectionFront]
  def unmkCollectionFront(p: Rep[Front]): Option[(Rep[Collection[Int]], Rep[BitSet])]

  // elem for concrete class
  class MapBasedFrontElem(val iso: Iso[MapBasedFrontData, MapBasedFront])
    extends FrontElem[MapBasedFront]
    with ConcreteElem[MapBasedFrontData, MapBasedFront] {
    override lazy val parent: Option[Elem[_]] = Some(frontElement)
    override lazy val entityDef = {
      val module = getModules("Fronts")
      module.concreteSClasses.find(_.name == "MapBasedFront").get
    }
    override lazy val tyArgSubst: Map[String, TypeDesc] = {
      Map()
    }

    override def convertFront(x: Rep[Front]) = // Converter is not generated by meta
!!!("Cannot convert from Front to MapBasedFront: missing fields List(mmap)")
    override def getDefaultRep = super[ConcreteElem].getDefaultRep
    override lazy val tag = {
      weakTypeTag[MapBasedFront]
    }
  }

  // state representation type
  type MapBasedFrontData = MMap[Int,Unit]

  // 3) Iso for concrete class
  class MapBasedFrontIso
    extends Iso[MapBasedFrontData, MapBasedFront] {
    override def from(p: Rep[MapBasedFront]) =
      p.mmap
    override def to(p: Rep[MMap[Int,Unit]]) = {
      val mmap = p
      MapBasedFront(mmap)
    }
    lazy val defaultRepTo: Rep[MapBasedFront] = MapBasedFront(element[MMap[Int,Unit]].defaultRepValue)
    lazy val eTo = new MapBasedFrontElem(this)
  }
  // 4) constructor and deconstructor
  abstract class MapBasedFrontCompanionAbs extends CompanionBase[MapBasedFrontCompanionAbs] with MapBasedFrontCompanion {
    override def toString = "MapBasedFront"

    def apply(mmap: Rep[MMap[Int,Unit]]): Rep[MapBasedFront] =
      mkMapBasedFront(mmap)
  }
  object MapBasedFrontMatcher {
    def unapply(p: Rep[Front]) = unmkMapBasedFront(p)
  }
  def MapBasedFront: Rep[MapBasedFrontCompanionAbs]
  implicit def proxyMapBasedFrontCompanion(p: Rep[MapBasedFrontCompanionAbs]): MapBasedFrontCompanionAbs = {
    proxyOps[MapBasedFrontCompanionAbs](p)
  }

  implicit case object MapBasedFrontCompanionElem extends CompanionElem[MapBasedFrontCompanionAbs] {
    lazy val tag = weakTypeTag[MapBasedFrontCompanionAbs]
    protected def getDefaultRep = MapBasedFront
  }

  implicit def proxyMapBasedFront(p: Rep[MapBasedFront]): MapBasedFront =
    proxyOps[MapBasedFront](p)

  implicit class ExtendedMapBasedFront(p: Rep[MapBasedFront]) {
    def toData: Rep[MapBasedFrontData] = isoMapBasedFront.from(p)
  }

  // 5) implicit resolution of Iso
  implicit def isoMapBasedFront: Iso[MapBasedFrontData, MapBasedFront] =
    new MapBasedFrontIso

  // 6) smart constructor and deconstructor
  def mkMapBasedFront(mmap: Rep[MMap[Int,Unit]]): Rep[MapBasedFront]
  def unmkMapBasedFront(p: Rep[Front]): Option[(Rep[MMap[Int,Unit]])]

  registerModule(scalan.meta.ScalanCodegen.loadModule(Fronts_Module.dump))
}

// Seq -----------------------------------
trait FrontsSeq extends FrontsDsl with scalan.ScalanSeq {
  self: FrontsDslSeq =>
  lazy val Front: Rep[FrontCompanionAbs] = new FrontCompanionAbs with UserTypeSeq[FrontCompanionAbs] {
    lazy val selfType = element[FrontCompanionAbs]
  }

  case class SeqBaseFront
      (override val set: Rep[CollectionOverArray[Int]], override val bits: Rep[BitSet])

    extends BaseFront(set, bits)
        with UserTypeSeq[BaseFront] {
    lazy val selfType = element[BaseFront]
  }
  lazy val BaseFront = new BaseFrontCompanionAbs with UserTypeSeq[BaseFrontCompanionAbs] {
    lazy val selfType = element[BaseFrontCompanionAbs]
  }

  def mkBaseFront
      (set: Rep[CollectionOverArray[Int]], bits: Rep[BitSet]): Rep[BaseFront] =
      new SeqBaseFront(set, bits)
  def unmkBaseFront(p: Rep[Front]) = p match {
    case p: BaseFront @unchecked =>
      Some((p.set, p.bits))
    case _ => None
  }

  case class SeqListFront
      (override val set: Rep[CollectionOverList[Int]], override val bits: Rep[BitSet])

    extends ListFront(set, bits)
        with UserTypeSeq[ListFront] {
    lazy val selfType = element[ListFront]
  }
  lazy val ListFront = new ListFrontCompanionAbs with UserTypeSeq[ListFrontCompanionAbs] {
    lazy val selfType = element[ListFrontCompanionAbs]
  }

  def mkListFront
      (set: Rep[CollectionOverList[Int]], bits: Rep[BitSet]): Rep[ListFront] =
      new SeqListFront(set, bits)
  def unmkListFront(p: Rep[Front]) = p match {
    case p: ListFront @unchecked =>
      Some((p.set, p.bits))
    case _ => None
  }

  case class SeqCollectionFront
      (override val set: Rep[Collection[Int]], override val bits: Rep[BitSet])

    extends CollectionFront(set, bits)
        with UserTypeSeq[CollectionFront] {
    lazy val selfType = element[CollectionFront]
  }
  lazy val CollectionFront = new CollectionFrontCompanionAbs with UserTypeSeq[CollectionFrontCompanionAbs] {
    lazy val selfType = element[CollectionFrontCompanionAbs]
  }

  def mkCollectionFront
      (set: Rep[Collection[Int]], bits: Rep[BitSet]): Rep[CollectionFront] =
      new SeqCollectionFront(set, bits)
  def unmkCollectionFront(p: Rep[Front]) = p match {
    case p: CollectionFront @unchecked =>
      Some((p.set, p.bits))
    case _ => None
  }

  case class SeqMapBasedFront
      (override val mmap: Rep[MMap[Int,Unit]])

    extends MapBasedFront(mmap)
        with UserTypeSeq[MapBasedFront] {
    lazy val selfType = element[MapBasedFront]
  }
  lazy val MapBasedFront = new MapBasedFrontCompanionAbs with UserTypeSeq[MapBasedFrontCompanionAbs] {
    lazy val selfType = element[MapBasedFrontCompanionAbs]
  }

  def mkMapBasedFront
      (mmap: Rep[MMap[Int,Unit]]): Rep[MapBasedFront] =
      new SeqMapBasedFront(mmap)
  def unmkMapBasedFront(p: Rep[Front]) = p match {
    case p: MapBasedFront @unchecked =>
      Some((p.mmap))
    case _ => None
  }
}

// Exp -----------------------------------
trait FrontsExp extends FrontsDsl with scalan.ScalanExp {
  self: FrontsDslExp =>
  lazy val Front: Rep[FrontCompanionAbs] = new FrontCompanionAbs with UserTypeDef[FrontCompanionAbs] {
    lazy val selfType = element[FrontCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  case class ExpBaseFront
      (override val set: Rep[CollectionOverArray[Int]], override val bits: Rep[BitSet])

    extends BaseFront(set, bits) with UserTypeDef[BaseFront] {
    lazy val selfType = element[BaseFront]
    override def mirror(t: Transformer) = ExpBaseFront(t(set), t(bits))
  }

  lazy val BaseFront: Rep[BaseFrontCompanionAbs] = new BaseFrontCompanionAbs with UserTypeDef[BaseFrontCompanionAbs] {
    lazy val selfType = element[BaseFrontCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object BaseFrontMethods {
    object contains {
      def unapply(d: Def[_]): Option[(Rep[BaseFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[BaseFrontElem] && method.getName == "contains" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[BaseFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[BaseFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object append {
      def unapply(d: Def[_]): Option[(Rep[BaseFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[BaseFrontElem] && method.getName == "append" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[BaseFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[BaseFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object BaseFrontCompanionMethods {
  }

  def mkBaseFront
    (set: Rep[CollectionOverArray[Int]], bits: Rep[BitSet]): Rep[BaseFront] =
    new ExpBaseFront(set, bits)
  def unmkBaseFront(p: Rep[Front]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: BaseFrontElem @unchecked =>
      Some((p.asRep[BaseFront].set, p.asRep[BaseFront].bits))
    case _ =>
      None
  }

  case class ExpListFront
      (override val set: Rep[CollectionOverList[Int]], override val bits: Rep[BitSet])

    extends ListFront(set, bits) with UserTypeDef[ListFront] {
    lazy val selfType = element[ListFront]
    override def mirror(t: Transformer) = ExpListFront(t(set), t(bits))
  }

  lazy val ListFront: Rep[ListFrontCompanionAbs] = new ListFrontCompanionAbs with UserTypeDef[ListFrontCompanionAbs] {
    lazy val selfType = element[ListFrontCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object ListFrontMethods {
    object contains {
      def unapply(d: Def[_]): Option[(Rep[ListFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[ListFrontElem] && method.getName == "contains" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[ListFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[ListFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object append {
      def unapply(d: Def[_]): Option[(Rep[ListFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[ListFrontElem] && method.getName == "append" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[ListFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[ListFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object ListFrontCompanionMethods {
  }

  def mkListFront
    (set: Rep[CollectionOverList[Int]], bits: Rep[BitSet]): Rep[ListFront] =
    new ExpListFront(set, bits)
  def unmkListFront(p: Rep[Front]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: ListFrontElem @unchecked =>
      Some((p.asRep[ListFront].set, p.asRep[ListFront].bits))
    case _ =>
      None
  }

  case class ExpCollectionFront
      (override val set: Rep[Collection[Int]], override val bits: Rep[BitSet])

    extends CollectionFront(set, bits) with UserTypeDef[CollectionFront] {
    lazy val selfType = element[CollectionFront]
    override def mirror(t: Transformer) = ExpCollectionFront(t(set), t(bits))
  }

  lazy val CollectionFront: Rep[CollectionFrontCompanionAbs] = new CollectionFrontCompanionAbs with UserTypeDef[CollectionFrontCompanionAbs] {
    lazy val selfType = element[CollectionFrontCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object CollectionFrontMethods {
    object contains {
      def unapply(d: Def[_]): Option[(Rep[CollectionFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[CollectionFrontElem] && method.getName == "contains" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[CollectionFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[CollectionFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object append {
      def unapply(d: Def[_]): Option[(Rep[CollectionFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[CollectionFrontElem] && method.getName == "append" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[CollectionFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[CollectionFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object CollectionFrontCompanionMethods {
  }

  def mkCollectionFront
    (set: Rep[Collection[Int]], bits: Rep[BitSet]): Rep[CollectionFront] =
    new ExpCollectionFront(set, bits)
  def unmkCollectionFront(p: Rep[Front]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: CollectionFrontElem @unchecked =>
      Some((p.asRep[CollectionFront].set, p.asRep[CollectionFront].bits))
    case _ =>
      None
  }

  case class ExpMapBasedFront
      (override val mmap: Rep[MMap[Int,Unit]])

    extends MapBasedFront(mmap) with UserTypeDef[MapBasedFront] {
    lazy val selfType = element[MapBasedFront]
    override def mirror(t: Transformer) = ExpMapBasedFront(t(mmap))
  }

  lazy val MapBasedFront: Rep[MapBasedFrontCompanionAbs] = new MapBasedFrontCompanionAbs with UserTypeDef[MapBasedFrontCompanionAbs] {
    lazy val selfType = element[MapBasedFrontCompanionAbs]
    override def mirror(t: Transformer) = this
  }

  object MapBasedFrontMethods {
    object contains {
      def unapply(d: Def[_]): Option[(Rep[MapBasedFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[MapBasedFrontElem] && method.getName == "contains" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[MapBasedFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[MapBasedFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object append {
      def unapply(d: Def[_]): Option[(Rep[MapBasedFront], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[MapBasedFrontElem] && method.getName == "append" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[MapBasedFront], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[MapBasedFront], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object set {
      def unapply(d: Def[_]): Option[Rep[MapBasedFront]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[MapBasedFrontElem] && method.getName == "set" =>
          Some(receiver).asInstanceOf[Option[Rep[MapBasedFront]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[MapBasedFront]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object MapBasedFrontCompanionMethods {
  }

  def mkMapBasedFront
    (mmap: Rep[MMap[Int,Unit]]): Rep[MapBasedFront] =
    new ExpMapBasedFront(mmap)
  def unmkMapBasedFront(p: Rep[Front]) = p.elem.asInstanceOf[Elem[_]] match {
    case _: MapBasedFrontElem @unchecked =>
      Some((p.asRep[MapBasedFront].mmap))
    case _ =>
      None
  }

  object FrontMethods {
    object contains {
      def unapply(d: Def[_]): Option[(Rep[Front], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[FrontElem[_]] && method.getName == "contains" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[Front], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[Front], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object append {
      def unapply(d: Def[_]): Option[(Rep[Front], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(v, _*), _) if receiver.elem.isInstanceOf[FrontElem[_]] && method.getName == "append" =>
          Some((receiver, v)).asInstanceOf[Option[(Rep[Front], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[Front], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object set {
      def unapply(d: Def[_]): Option[Rep[Front]] = d match {
        case MethodCall(receiver, method, _, _) if receiver.elem.isInstanceOf[FrontElem[_]] && method.getName == "set" =>
          Some(receiver).asInstanceOf[Option[Rep[Front]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Front]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }

  object FrontCompanionMethods {
    object emptyBaseFront {
      def unapply(d: Def[_]): Option[Rep[Int]] = d match {
        case MethodCall(receiver, method, Seq(len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "emptyBaseFront" =>
          Some(len).asInstanceOf[Option[Rep[Int]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Int]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object emptyListBasedFront {
      def unapply(d: Def[_]): Option[Rep[Int]] = d match {
        case MethodCall(receiver, method, Seq(len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "emptyListBasedFront" =>
          Some(len).asInstanceOf[Option[Rep[Int]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Int]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object emptyCollBasedFront {
      def unapply(d: Def[_]): Option[Rep[Int]] = d match {
        case MethodCall(receiver, method, Seq(len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "emptyCollBasedFront" =>
          Some(len).asInstanceOf[Option[Rep[Int]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Int]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object emptyMapBasedFront {
      def unapply(d: Def[_]): Option[Rep[Int]] = d match {
        case MethodCall(receiver, method, Seq(len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "emptyMapBasedFront" =>
          Some(len).asInstanceOf[Option[Rep[Int]]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[Rep[Int]] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object fromStartNode {
      def unapply(d: Def[_]): Option[(Rep[Int], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(start, len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "fromStartNode" =>
          Some((start, len)).asInstanceOf[Option[(Rep[Int], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[Int], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }

    object fromStartNodeMap {
      def unapply(d: Def[_]): Option[(Rep[Int], Rep[Int])] = d match {
        case MethodCall(receiver, method, Seq(start, len, _*), _) if receiver.elem == FrontCompanionElem && method.getName == "fromStartNodeMap" =>
          Some((start, len)).asInstanceOf[Option[(Rep[Int], Rep[Int])]]
        case _ => None
      }
      def unapply(exp: Exp[_]): Option[(Rep[Int], Rep[Int])] = exp match {
        case Def(d) => unapply(d)
        case _ => None
      }
    }
  }
}

object Fronts_Module {
  val packageName = "scalan.graphs"
  val name = "Fronts"
  val dump = "H4sIAAAAAAAAAN1WTWwbRRgdbxw7a6dJMaIiuVBS81NEbQsJ9ZADSlwHVXLiKNsiZCqkyXrsTNmd3cxMIptDDxzhhjhwQaj33rggIXFBSIgDJwRInDkVEKoKPYH4Zna86zXZYg4BCR9GszOf53vfe29+7vyE5gVHTwsXe5jVfCJxzdH9DSGrTotJKkfbQe/II1dI/61zH7vbbFNYaLmLCgdYXBFeF9lRpzUM475DDtvIxswlQgZcSPRkW2eou4HnEVfSgNWp7x9JvO+RepsKud5G+f2gNzpEt1Cujc66AXM5kcRpelgIIsz4AlGIaPxt6+9RJ0xysLqqoj5RxTWOqQT4kONsFL9HQmfEAjbyJVoy0DqhggUxReqHAZfjFEVY7iDojT/zDMMAqrRv4mNchxSDuiM5ZQP4ZznE7ht4QHYgRIXnAbAgXv/aKNTfc21UEuQQCLrqh54eGYYIIVDgBQ2ilvBTi/mpKX6qDuEUe/RNrCZ3eTAcoeiXm0NoGMISz//NEuMVSIv1qm/fcF974JR9S/15qKAUdYUFWOiJDDdoKYDHL/beFfdevn3ZQqUuKlGxsS8kx66clNywVcaMBVJjjgnEfABqrWWppbNsQMyUJWw38EPMYCVD5SLo5FGXShWsxhaNOhnUF2VIxqG5YZiL6z2fUa/2TRN73u7dlUtP/dh61UJWOoUNSzpgfD5eVKL5LR4wqQlVjW24zc4S1/vM3Z97nzfQDStmySw6mzCwxLz47pvy1xdfstBCV9t4y8ODLhAlWh7xO7wJyLpoITgmPJopHmNP9U4UqtgjfXzkSUPfZN1zULdE5zM3XEgUKeva3LkxAeXInzsBI9Wt3epvzpfv3VH242gxmol24B/08u/fL/WldqZEc4JEW3EZ+rBvDRlm5NFm7PYOVLXBOR7FdF3I0jUku5z6cI4ckxc/++T6L5/uzGtpK6biV7B3RKJdbQpOileYcg3AcjVSORLYHuqsj8X1qmZVgoGpFNnwC5sUtuW0WxLPlCJinMAnj6zdo6/ffkdqd+SG6ROos38TSFjX/1t5iFHGJ+Gv3YZ1f+XbDy1kgx8Ao4/DamPG/XuKexKl6VlqmltAG7yRnjxxoxnqEglArcomFkQHNyfRriaHXkV3JbLjyKmAcm6G3Gatici/2OHhZq6kzazO7ChANRdO2WyqvaTbxumooMqZTYU48j9RoZSo8P9h//GkqJk0WJ6KPyUl8j4cPNmc5be3zXQkgu6uSWQ9exEmrzMqp6v+t/g8B8DUYdGbic0zqegkaMJOhRPB2nuE9ql6wf1DnCkC0vDjGpaysKdeMatTWd5PD0J1BR0LT7oz5tYZcBweCAOBo7WMy8gxVwHcR7cefLDz3Fcf/aBv4ZK6VOCRwOIn+OTtO0VQlBte1BMwwRvqmtFA/wTrZUF+4QwAAA=="
}
}

trait FrontsDsl extends impl.FrontsAbs {self: FrontsDsl =>}
trait FrontsDslSeq extends impl.FrontsSeq {self: FrontsDslSeq =>}
trait FrontsDslExp extends impl.FrontsExp {self: FrontsDslExp =>}
