# Covariance Matrix Adaptation Evolution Strategy

This project is a minimalistic Scala version of the CMA-ES algorithm as described in 
[https://www.lri.fr/~hansen/cmatutorial.pdf]. This implementation is not as full as Java version however it provides certain 
benefits compared to the latter one:

* It is much more compact and straightforward.
* Its objective function can be run in parrallel. This might help to reduce optimization time if takes
long time to compute your fitness function.
* The strategy implementation is stateless and idempotent.

Here is a minimal working example of library usage:
 
 ```scala
  
   import com.vorlov.cmaes._
 
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
     0.05, // initial value
     0.2, // initial standard deviation
     iterationsExceeded(6000) orElse lowVariance(1e-14) orElse countIteration orElse proceed // stop condition
     )
   
   ```

   