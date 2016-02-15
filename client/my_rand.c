/* my_rand.c 
   Ultima modifica: 19/7/1999 */

#include <math.h> 
#include "my_rand.h"

/* Define the constants. */

#define MODLUS 2147483647
#define MULT1       24112
#define MULT2       26143

/* Set the default seeds for all 100 streams. */

static long zrng[] =
{         0,
 1973272912,  281629770,   20006270, 1280689831, 2096730329, 1933576050,
  913566091,  246780520, 1363774876,  604901985, 1511192140, 1259851944,
  824064364,  150493284,  242708531,   75253171, 1964472944, 1202299975,
  233217322, 1911216000,  726370533,  403498145,  993232223, 1103205531,
  762430696, 1922803170, 1385516923,   76271663,  413682397,  726466604,
  336157058, 1432650381, 1120463904,  595778810,  877722890, 1046574445,
   68911991, 2088367019,  748545416,  622401386, 2122378830,  640690903,
 1774806513, 2132545692, 2079249579,   78130110,  852776735, 1187867272,
 1351423507, 1645973084, 1997049139,  922510944, 2045512870,  898585771,
  243649545, 1004818771,  773686062,  403188473,  372279877, 1901633463,
  498067494, 2087759558,  493157915,  597104727, 1530940798, 1814496276,
  536444882, 1663153658,  855503735,   67784357, 1432404475,  619691088,
  119025595,  880802310,  176192644, 1116780070,  277854671, 1366580350,
 1142483975, 2026948561, 1053920743,  786262391, 1792203830, 1494667770,
 1923011392, 1433700034, 1244184613, 1147297105,  539712780, 1545929719,
  190641742, 1645390429,  264907697,  620389253, 1502074852,  927711160,
  364849192, 2049576050,  638580085,  547070247 };



/* The following three declarations are for use of the random-number
   generator my_uniform and the associated functions uniform_set and
   uniform_get for seed management.

	float my_uniform(int stream);
	void uniform_set(long zset, int stream);
	long uniform_get(int stream); */

/* Prime modulus multiplicative linear congruential generator
   Z[i]=(630360016*Z[i-1])(mod(pow(2,31)-1)), based on Marse and
   Roberts' portable FORTRAN random-number generator UNIRAN. Multiple
   (100) streams are supported, with seeds spaced 100,000 apart.
   Throughout, input arguments "stream" must be an int giving the
   desired stream number.

   Usage:  (Three functions)

   1. To obtain the next U(0,1) random number from stream "stream",
	execute
      u=my_uniform(stream);
      where my_uniform is a float function. The float variable u will
      contain the next random number.

   2. To set the seed for stream "stream" to a desired value zset,
        execute
      uniform_set(zset, stream);
      where uniform_set is a void function and zset must be a long set to
      the desired seed, a number between 1 and 2147483646 (inclusive).
      Default seeds for all 100 stream are given in the code.

   3. To get the current (most recently used) integer in the sequence
      being generated for stream "stream" into the long variable zset,
        execute
      zget=uniform_get(stream);
      where uniform_get is a long function.
*/


/* Generate the next random number. */

float my_uniform(int stream)
{
  long zi, lowprd, hi31;

  zi=zrng[stream];
  lowprd=(zi & 65535) * MULT1;
  hi31=(zi >> 16) * MULT1 + (lowprd >> 16);
  zi=((lowprd & 65535) - MODLUS) + ((hi31 & 32767) << 16) + (hi31 >> 15);
  if (zi < 0) zi+=MODLUS;
  lowprd=(zi & 65535) * MULT2;
  hi31=(zi >> 16) * MULT2 + (lowprd >> 16);
  zi=((lowprd & 65535) - MODLUS) + ((hi31 & 32767) << 16) + (hi31 >> 15);
  if (zi < 0) zi+=MODLUS;
  zrng[stream]=zi;
  return ((zi >> 7 | 1) + 1) / 16777216.0;
}

/* Set the current zrng for stream "stream" to zset. */

void uniform_set(long zset, int stream)
{
  zrng[stream]=zset;
}

/* Return the current zrng for stream "stream" */

long uniform_get(int stream)
{
  return zrng[stream];
}

/*************************************************************************/

/* Distribuzione uniforme tra a e b (float) */

float my_uniform_range(int stream, float a, float b)
{
  float unif, unif_range;

  unif = my_uniform(stream);
  unif_range = a + (b-a)*unif;
  return unif_range;
}

/* ---------------------------------------------------------- */

/* Distribuzione esponenziale con media mean_exp */

float my_expntl(int stream, float mean_exp)
{
  float unif,expon;

  unif=my_uniform(stream);
  expon=-mean_exp*log((double)unif);
  return expon;
}

/* ---------------------------------------------------------- */

/* Distribuzione uniforme discreta tra a e b (interi) */

int my_random(int stream, int a, int b)
{
  float unif;
  int rand_num;

  unif=my_uniform(stream);
  rand_num=a+(int)floor((double)(b-a+1)*(double)unif);
  return rand_num;
}

/* ---------------------------------------------------------- */

/* Distribuzione di Weibull (a:shape parameter, b:scale parameter) */

float my_weibull(int stream, float a, float b)
{
  float unif, weibull;

  unif=my_uniform(stream);
  weibull=b*(float)pow(-log((double)unif), (double)(1./a));
  return weibull;
}

/* ---------------------------------------------------------- */

/* Distribuzione di Pareto (a:shape parameter, k:minima osservazione) */

double my_pareto(int stream, double a, double k)
{
  double pareto;
  float unif;

  unif=my_uniform(stream);
  pareto=k*pow((double)unif,(-1./a));
  return pareto;
}

/* ---------------------------------------------------------- */

/* Distribuzione di Pareto (a:shape parameter, 
                            k:minima osservazione, p:massima osservazione) */

double my_bounded_pareto(int stream, double a, double k, double p)
{
  double pareto;
  float unif, den; 

  unif=my_uniform(stream);
  den = 1 - (unif*(1 - pow((k/p),a)));
  pareto=k*pow((double)den,(-1./a));
  return pareto;
}




/* ---------------------------------------------------------- */

/* Distribuzione normale standard 
        Metodo polare: coppia di variabili */

double my_stdnormal(int stream)
{
  static int iset = 0;
  static double gset;
  double y, w, v1, v2;
  
  if (iset==0)
  {
    do
    {
       v1 = 2.0 * my_uniform(stream) - 1.0;
       v2 = 2.0 * my_uniform(stream) - 1.0;       
       w = v1*v1 + v2*v2;
    }
    while (w>=1.0);
    y = sqrt ((-2.0 * log(w))/w);
    gset = v1 * y;
    iset = 1;
    return (v2*y);
  }
  else
  {
    iset = 0;
    return gset;
  }
}

/* ---------------------------------------------------------- */

/* Distribuzione normale */

double my_normal(int stream, double mu, double sigma)
{
  double norm;


  norm = mu + sigma*my_stdnormal(stream);
  return norm;
}    
   
/* ---------------------------------------------------------- */

/* Distribuzione gaussiana inversa */

float my_invgaussian(int stream, float mu, float lambda)
{

  float stdnorm, v, w, c, x;
  float p;
  
  stdnorm=(float)my_stdnormal(stream);
  v = stdnorm*stdnorm;           /* chi quadro with one degree of freedom */

  w = mu*v;
  c = mu/(2.0*lambda);

  x = mu + c*(w -sqrt(w*(4.0*lambda + w)));

  p = mu/(mu + x);              /* Bernoulli trials */
  if (p > my_uniform(stream)) 
    return (x);
  else return(mu*mu/x);
  
}

/* ---------------------------------------------------------- */

/* Distribuzione lognormale */

double my_lognormal(int stream, double mu, double sigma)
{
  double norm, lognorm;
  
  norm = my_normal(stream, mu, sigma);
  lognorm = exp(norm);
  return lognorm;
}  

/* ---------------------------------------------------------- */

/************** ZIPF  ******************/

float set_kzipf(int max_rank, float alpha)
{
  int i;
  float k_zipf;

  k_zipf = 0.;	
  for (i=1; i<=max_rank; i++)
    k_zipf += 1./pow((double)i, (double)(1.-alpha));

  k_zipf = 1./(float)k_zipf;
  return k_zipf;
}


float my_PDF_zipf(int rank, float k_zipf, float alpha)
{
  float zipf;

  zipf = k_zipf/pow((double)rank, (double)(1.-alpha));
  return zipf;
}


int my_zipf(int stream, float *popularity, int max_rank)
{
  float icdf;
  int i;
  float unif;

  i = 0;
  icdf = 0.;
  unif=my_uniform(stream);

  while ((icdf <= unif)&&(i < max_rank))
  {
    icdf += popularity[i];
    i++;
  }
  i--; 
  return(i);		
}

/* ---------------------------------------------------------- */

/* Distribuzione gamma */

double my_gamma(int stream, double alpha, double beta)
{
  double gam, x, y;
  float unif1, unif2, p, a, b, q, theta, d, v, w, z; 
  int flag;
  
  /* gamma(alpha, beta) = beta * gamma(alpha, 1) */
  
  /* Generation from the gamma(alpha, 1) distribution*/
  if (alpha == 1.) 
    x = (double)my_expntl(stream, alpha);
  else
   
    if ((alpha > 0.) &&  (alpha < 1.))
    {
      b = (exp(1.) + alpha)/exp(1.);
      flag = 1;
      
      while (flag)
      {
        unif1 = my_uniform(stream);
        p = b*unif1;
        if (p <= 1.)
	{
	  y = pow((double)p, (1./alpha));
	  unif2 = my_uniform(stream);
	  if (unif2 <= exp(-y))
	  {
	    x = y;
	    flag = 0;
	  }
	}
	else
	{
	  y = log((double)((b-p)/alpha));
	  unif2 = my_uniform(stream);
	  if (unif2 <= pow(y, (alpha - 1.)))
	  {
	    x = y;
	    flag = 0;
	  }
	}
      }
    }

    else /* alpha > 1. */
    {
      a = 1./sqrt(2.*alpha-1.);
      b = alpha - log(4.);
      q = alpha + (1./a);
      theta = 4.5;
      d = 1. + log((double)(theta));
      flag = 1;
      
      while (flag)
      {
        unif1 = my_uniform(stream);
        unif2 = my_uniform(stream);

        v = a*log((double)(unif1/(1. - unif1)));
	y = alpha*exp((double)v);
	z = pow((double)unif1, 2.) * unif2;
	w = b + q*v - y;

	if ((w + d - theta*z) >=0)
	{
	  x = y;
	  flag = 0;
	}
	else
	  if (w >= log((double)(z)))
	  {
	    x = y;
	    flag = 0;
	  }
      }
    }
      
  gam = beta * x;
  return gam;
}
