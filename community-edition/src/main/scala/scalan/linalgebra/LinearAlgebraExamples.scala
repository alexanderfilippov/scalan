package scalan.linalgebra

import scalan.ScalanCommunityDsl

trait LinearAlgebraExamples extends MatricesDsl { self: ScalanCommunityDsl =>
  def mvm[T](matrix: Matrix[T], vector: Vector[T])(implicit eT: Elem[T], n: Numeric[T]): Vector[T] =
    DenseVector(matrix.rows.map { r => r dot vector })

  lazy val ddmvm = fun { p: Rep[(Array[Array[Double]], Array[Double])] =>
    val Pair(m, v) = p
    val matrix: Matrix[Double] = RowMajorMatrix(Collection(m.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    val vector: Vector[Double] = DenseVector(Collection(v))
    (matrix * vector).items.arr
  }

  lazy val dsmvm = fun { p: Rep[(Array[Array[Double]], (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val matrix: Matrix[Double] = RowMajorMatrix(Collection(m.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    (matrix * vector).items.arr
  }

  lazy val sdmvm = fun { p: Rep[(Array[(Array[Int], (Array[Double], Int))], Array[Double])] =>
    val Pair(m, v) = p
    val width = m(0)._3
    val matrix: Matrix[Double] = RowMajorSparseMatrix(Collection(m.map {
      r: Rep[(Array[Int], (Array[Double], Int))] =>
      SparseVector(Collection(r._1), Collection(r._2), r._3).convertTo[AbstractVector[Double]]}), width)
    val vector: Vector[Double] = DenseVector(Collection(v))
    (matrix * vector).items.arr
  }

  lazy val ssmvm = fun { p: Rep[(Array[(Array[Int], (Array[Double], Int))], (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val width = m(0)._3
    val matrix: Matrix[Double] = RowMajorSparseMatrix(Collection(m.map {
      r: Rep[(Array[Int], (Array[Double], Int))] =>
        SparseVector(Collection(r._1), Collection(r._2), r._3) }), width)
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    (matrix * vector).items.arr
  }

  lazy val fdmvm = fun { p: Rep[((Array[Double], Int), Array[Double])] =>
    val Pair(m, v) = p
    val matrix: Matrix[Double] = RowMajorFlatMatrix(Collection(m._1), m._2)
    val vector: Vector[Double] = DenseVector(Collection(v))
    (matrix * vector).items.arr
  }

  lazy val fsmvm = fun { p: Rep[((Array[Double], Int), (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val matrix: Matrix[Double] = RowMajorFlatMatrix(Collection(m._1), m._2)
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    (matrix * vector).items.arr
  }

  lazy val ddmvm0 = fun { p: Rep[(Array[Array[Double]], Array[Double])] =>
    val Pair(m, v) = p
    val matrix: Matrix[Double] = RowMajorMatrix(Collection(m.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    val vector: Vector[Double] = DenseVector(Collection(v))
    mvm(matrix, vector).items.arr
  }

  lazy val dsmvm0 = fun { p: Rep[(Array[Array[Double]], (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val matrix: Matrix[Double] = RowMajorMatrix(Collection(m.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    mvm(matrix, vector).items.arr
  }

  lazy val sdmvm0 = fun { p: Rep[(Array[(Array[Int], (Array[Double], Int))], Array[Double])] =>
    val Pair(m, v) = p
    val width = m(0)._3
    val matrix: Matrix[Double] = RowMajorSparseMatrix(Collection(m.map {
      r: Rep[(Array[Int], (Array[Double], Int))] =>
        SparseVector(Collection(r._1), Collection(r._2), r._3) }), width)
    val vector: Vector[Double] = DenseVector(Collection(v))
    mvm(matrix, vector).items.arr
  }

  lazy val ssmvm0 = fun { p: Rep[(Array[(Array[Int], (Array[Double], Int))], (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val width = m(0)._3
    val matrix: Matrix[Double] = RowMajorSparseMatrix(Collection(m.map {
      r: Rep[(Array[Int], (Array[Double], Int))] =>
        SparseVector(Collection(r._1), Collection(r._2), r._3) }), width)
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    mvm(matrix, vector).items.arr
  }

  lazy val fdmvm0 = fun { p: Rep[((Array[Double], Int), Array[Double])] =>
    val Pair(m, v) = p
    val matrix: Matrix[Double] = RowMajorFlatMatrix(Collection(m._1), m._2)
    val vector: Vector[Double] = DenseVector(Collection(v))
    mvm(matrix, vector).items.arr
  }

  lazy val fsmvm0 = fun { p: Rep[((Array[Double], Int), (Array[Int], (Array[Double], Int)))] =>
    val Tuple(m, vIs, vVs, vL) = p
    val matrix: Matrix[Double] = RowMajorFlatMatrix(Collection(m._1), m._2)
    val vector: Vector[Double] = SparseVector(Collection(vIs), Collection(vVs), vL)
    mvm(matrix, vector).items.arr
  }

  lazy val ddmmm = fun { p: Rep[(Array[Array[Double]], Array[Array[Double]])] =>
    val Pair(m1, m2) = p
    val matrix1 = RowMajorMatrix(Collection(m1.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    val matrix2 = RowMajorMatrix(Collection(m2.map { r: Arr[Double] => DenseVector(Collection(r)) }))
    (matrix1 * matrix2).rows.arr.map(_.items.arr)
  }

  lazy val ssmmm = fun { p: Rep[(Array[(Array[Int], (Array[Double], Int))], Array[(Array[Int], (Array[Double], Int))])] =>
    val Pair(m1, m2) = p
    val width1 = m1(0)._3
    val width2 = m2(0)._3
    val matrix1 = RowMajorSparseMatrix(Collection(m1.map { r => SparseVector(Collection(r._1), Collection(r._2), r._3) }), width1)
    val matrix2 = RowMajorSparseMatrix(Collection(m2.map { r => SparseVector(Collection(r._1), Collection(r._2), r._3) }), width2)
    (matrix1 * matrix2).rows.arr.map(_.items.arr)
  }

  lazy val ffmmm = fun { p: Rep[((Array[Double], Int), (Array[Double], Int))] =>
    val Pair(m1, m2) = p
    val matrix1 = RowMajorFlatMatrix(Collection(m1._1), m1._2)
    val matrix2 = RowMajorFlatMatrix(Collection(m2._1), m2._2)
    (matrix1 * matrix2).rows.arr.map(_.items.arr)
  }
}
