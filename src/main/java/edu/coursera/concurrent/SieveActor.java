package edu.coursera.concurrent;

import edu.rice.pcdp.*;
//import edu.rice.pcdp;
//import edu.rice.pcdp.PCDP.finish;
import java.util.ArrayList;
import java.util.List;
import static edu.rice.pcdp.PCDP.async;

import static edu.rice.pcdp.PCDP.finish;
//import static javafx.application.Platform.exit;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
//         List<Integer> result = new ArrayList<>();
         SieveActorActor sieveActor = new SieveActorActor(2);

//finish(())

//        finish(());
        finish(() -> {
//            async(()-> {
                for (int i = 3; i <= limit; i++) {
                    sieveActor.send(i);
                }
                sieveActor.send(0);
//            });
        });

         int numPrimes = 0;
         SieveActorActor loopActor = sieveActor;
         while(loopActor!=null){
             numPrimes += loopActor.numLocalPrimes();
             loopActor = loopActor.nextActor();
         }

         return numPrimes;



    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        private static final int MAX_LOCAL_PRIMES = 500;
        private final int localPrimes[];
        private  SieveActorActor nextActor;
        private int numLocalPrimes;
//        private

        SieveActorActor(final int localprime)
        {
            this.localPrimes = new int[MAX_LOCAL_PRIMES];
            this.localPrimes[0] = localprime;
            this.numLocalPrimes = 1;
            this.nextActor = null;
        }

        public SieveActorActor nextActor() {return nextActor;}

        public int numLocalPrimes() {return numLocalPrimes;}
        @Override
        public void process(final Object msg) {
//            throw new UnsupportedOperationException();
            final int candidate = (Integer) msg;
            if(candidate<=0) {
                if (nextActor != null) {
                    nextActor.send(msg);
//                    exit();
                }

            }else{

                    final boolean locallyPrime = isLocallyPrime(candidate);
                    if(locallyPrime){
                        if(numLocalPrimes()<MAX_LOCAL_PRIMES){
                            localPrimes[numLocalPrimes] = candidate;
                            numLocalPrimes+=1;
                        }
                        else if(nextActor == null) {
                            nextActor = new SieveActorActor(candidate);
                        }
                        else{
//                            nextActor.MAX_LOCAL_PRIMES = 2* MAX_LOCAL_PRIMES;
                            nextActor.send(msg);
                        }

                }
            }
        }

        private boolean isLocallyPrime(final int candidate){
            final boolean[] isPrime = {true};
            checkPrimeKernel(candidate, isPrime, 0, numLocalPrimes());
            return isPrime[0];
        }

        private void checkPrimeKernel(final int candidate, final boolean[] isPrime, int startIndex, int endIndex ){
            for(int i = startIndex; i<endIndex ;i++){
                if(candidate%localPrimes[i]==0){
                    isPrime[0]=false;
                }
            }

        }
    }
}
