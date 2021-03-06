package scalan.compilation.lms.scalac

import scala.virtualization.lms.common._
import scala.virtualization.lms.epfl.test7.ScalaGenFatArrayLoopsFusionOpt
import scala.virtualization.lms.epfl.test8.ScalaGenArrayMutation
import scalan.compilation.lms.common._
import scalan.compilation.lms.{LmsBackendFacade, BaseCodegen}


//import scalan.compilation.lms._
//import scalan.compilation.lms.common._

/**
 * Created by adel on 5/15/15.
 */


class ScalaCommunityCodegen[BackendCake <: LmsBackendFacade](backend: BackendCake) extends BaseCodegen[BackendCake]
  with ScalaGenObjectOpsExt             //from scalan.compilation.lms.common
  with ScalaGenArrayOps
  with ScalaGenListOps // todo may be owr ScalaGenLstOps should extend lms's ScalaGenListOps?
  with ScalaGenLstOps                   //from scalan.compilation.lms.common
  with ScalaGenNumericOps with ScalaGenPrimitiveOps with ScalaGenEqual  with ScalaGenOrderingOps
  with ScalaGenBooleanOps with ScalaGenStruct with ScalaGenStringOps
  with ScalaGenEitherOps               //from scalan.compilation.lms.common
  with ScalaGenTupleOps
  with ScalaGenFatArrayLoopsFusionOpt  ////todo may be it is better to copy-paste important part of code from this class, because scala.virtualization.lms.epfl.test7.ScalaGenFatArrayLoopsFusionOpt placed in [unit-]test package
  with ScalaGenArrayMutation
  with ScalaGenIfThenElseFat with LoopFusionOpt with ScalaGenCastingOps with ScalaGenMathOps
  with ScalaGenMethodCallOps          //from scalan.compilation.lms.common
  with ScalaGenHashMapOps  with ScalaGenIterableOps  with ScalaGenWhile with ScalaGenIfThenElse
  with ScalaGenVariables with ScalaGenArrayBuilderOps with ScalaGenExceptionOps with ScalaGenFunctions
  with ScalaGenRangeOps
  with ScalaGenMiscOps
  with ScalaGenVectorOps with ScalaGenExtNumOps with ScalaGenSystemOps //from scalan.compilation.lms.common
  with ScalaGenArrayOpsExt
{
  val IR: BackendCake = backend
  import IR._

  //def codeExtension: String = "scala"

  override def shouldApplyFusion(currentScope: List[Stm])(result: List[Exp[Any]]): Boolean = true

  private def isTuple2(name: String) = name.startsWith("Tuple2")

  override def remap[A](m: Manifest[A]) =
    if (m.equals(LmsType.wildCard)) "_"
    else if (isTuple2(m.runtimeClass.getSimpleName)) {
      if (m.typeArguments.length == 2) s"(${remap(m.typeArguments(0))}, ${remap(m.typeArguments(1))})"
      else m.toString
    }
    else super.remap(m)

  override def emitNode(sym: Sym[Any], rhs: Def[Any]) = rhs match {
    case Struct(ClassTag(name), elems) if isTuple2(name) =>
      emitValDef(sym, "(" + elems.map(e => quote(e._2)).mkString(",") + ")")
    case _ => super.emitNode(sym, rhs)
  }
}
