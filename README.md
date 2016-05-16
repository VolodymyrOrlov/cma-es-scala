# Covariance Matrix Adaptation Evolution Strategy

This library is a minimalistic Scala version of the CMA-ES algorithm as described in 
[this paper](http://arxiv.org/pdf/1604.00772v1.pdf). The implementation is not as full as Java version of the  
 algorithm however it provides certain benefits compared to the latter one:

* This implementation is more compact and straightforward.
* In Scala version objective function can be evaluated in parallel. This might help to reduce optimization time if it 
takes long time to compute your fitness function.
* The strategy implementation is stateless and idempotent.

## Using CMA-ES

This project can be built with sbt 0.13. Add these lines to your SBT project definition:

```scala

resolvers += Resolver.bintrayRepo("volodymyr-orlov", "maven")

libraryDependencies ++= Seq(
  "com.sungevity" %% "cma-es-scala" % "1.0.1"
)

```

Here is a minimal working example.:
 
 ```scala
  
   import com.sungevity.cmaes._
   import breeze.linalg._
 
   val rosenbrock = ParallelObjectiveFunction ({ // a non-convex function we want to optimize
     case x: DenseVector[Double] => {
           (for (i <- 0 until x.length - 1) yield {
             100 * (x(i) * x(i) - x(i + 1)) * (x(i) * x(i) - x(i + 1)) +
               (x(i) - 1.0) * (x(i) - 1.0);
           }).sum
     }
   })
   
   val driver = CMAESDriver(rosenbrock) // instantiate a driver  
   
   val result = driver.optimize(5, // population size
     5, // search space size
     0.05, // initial value
     0.2, // initial standard deviation
     iterationsExceeded(6000) orElse lowVariance(1e-14) orElse minFitnessReached(1e-14) orElse proceed // stop condition
     )
   
   ```

   
