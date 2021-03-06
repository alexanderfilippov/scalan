package scalan
package compilation
package lms
package cxx
package sharedptr

import java.io.File

import scalan.it.smoke.SmokeItTests

class CxxShptrLmsSmokeItTests extends SmokeItTests {
  class ProgExp extends Prog with ScalanCtxExp with ScalanCommunityExp {
    lazy val arrayForeach = fun {arr:Rep[Array[Double]] =>
//      val init = SArray.replicate(1, 0.0)
      arr.fold(arr, {p:Rep[(Array[Double],Double)] => p._1.update(0, p._1(0) + p._2 + 1.0)})
    }

    lazy val testList = fun {in:Rep[List[Array[Double]]] =>
      in.map{a => a.map({v => v*2.0})}
    }

    lazy val testStringDuplicate = fun {str:Rep[String] => (str + str + "duplicate").startsWith("hello")}
  }

  val progStaged = new LmsCompilerCxx(new ProgExp) with CoreBridge
  import progStaged.defaultCompilerConfig

  test("testStringDuplicate") {
    val in = "word_"
    val functionName = "testStringDuplicate"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.testStringDuplicate, GraphVizConfig.default)
  }

  test("testList") {
    val in = 2
    val functionName = "testList"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.testList, GraphVizConfig.default)
  }

  test("arrayForeach") {
    val in = 2
    val functionName = "arrayForeach"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.arrayForeach, GraphVizConfig.default)
    //    compareOutputWithSequential(progStaged)(progSeq.simpleArith, progStaged.simpleArith, "simpleArith", in)
  }

  test("test10simpleSum") {
    val in = 7
    val functionName = "simpleSum"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.simpleSum, GraphVizConfig.default)
    //    compareOutputWithSequential(progStaged)(progSeq.simpleSum, progStaged.simpleSum, "simpleSum", in)
  }
  test("test11optionOps") {
    val in = 7
    val functionName = "optionOps"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.optionOps, GraphVizConfig.default)
    //    compareOutputWithSequential(progStaged)(progSeq.simpleOptionOps, progStaged.simpleOptionOps, "simpleOptionOps", in)
  }
  test("lambdaApply") {
    val x = 7
    val f = (_: Int) * 2
    val functionName = "lambdaApply"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.lambdaApply, GraphVizConfig.default)
    //    compareOutputWithSequential(progStaged)(progSeq.lambdaApply, progStaged.lambdaApply, "lambdaApply", (x, f))
  }
  test("lambdaConst") {
    val in = 7
    val functionName = "lambdaConst"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.lambdaConst, GraphVizConfig.default)
    //    getStagedOutput(progStaged)(progStaged.lambdaConst, "lambdaConst", in).isInstanceOf[Right[_, _]]
  }

  test("arrayUpdate") {
    val in = Array(0, 0)
    val functionName = "arrayUpdate"
    val dir = new File(prefix, functionName)
    progStaged.buildExecutable(dir, dir, functionName, progStaged.scalan.arrayUpdate, GraphVizConfig.default)
    //    compareOutputWithSequential(progStaged)(progSeq.arrayUpdate, progStaged.arrayUpdate, "arrayUpdate", in)
  }
}
