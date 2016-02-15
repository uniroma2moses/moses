/* my_rand.h 
   Ultima modifica: 15/3/1999 */

extern void    uniform_set(long zset, int stream);

extern float   my_uniform(int stream);

extern float   my_uniform_range(int stream, float a, float b);

extern float   my_expntl(int stream, float mean_exp);

extern int     my_random(int stream, int a, int b);

extern float   my_weibull(int stream, float a, float b);

extern double  my_pareto(int stream, double a, double k);

extern double  my_bounded_pareto(int stream, double a, double k, double p);

extern double  my_stdnormal(int stream);

extern double  my_normal(int stream, double mu, double sigma);

extern float   my_invgaussian(int stream, float mu, float lambda);

extern double  my_lognormal(int stream, double mu, double sigma);

extern double  my_gamma(int stream, double alpha, double beta);

/* Zipf */
extern float   set_kzipf(int max_rank, float alpha);
float          my_PDF_zipf(int rank, float k_zipf, float alpha);
int            my_zipf(int stream, float *popolarity, int max_rank);
