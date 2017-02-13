package com.trivadis.notes.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("primes")
public class PrimesResource {

	@GET
	public Response calcPrimes(@QueryParam("count") int numberOfPrimes) {
		numberOfPrimes = Math.max(5, numberOfPrimes);
		long[] primes = new long[numberOfPrimes];

		primes[0] = 2;
		primes[1] = 3;
		primes[2] = 5;
		primes[3] = 7;

		long number = 9;
		for (int i = 4; i < numberOfPrimes; i++) {
			boolean isPrime = true;
			do {
				isPrime = true;
				number += 2;
				
				for (int j = 0; isPrime && j < i; j++) {
					if (number % primes[j] == 0)
						isPrime = false;
				}
				
			} while (!isPrime);
			
			primes[i] = number;
		}

		return Response.ok(primes, MediaType.APPLICATION_JSON).build();
	}
}
