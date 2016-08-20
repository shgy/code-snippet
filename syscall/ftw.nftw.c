#define _XOPEN_SOURCE 600
#include<stdio.h>
#include<string.h>
#include<stdlib.h>
#include<errno.h>

#include<ftw.h>

/*
 
 */

int nftw_handler(const char *fpath, const struct stat *sb,
  				int typeflag, struct FTW *ftwbuf)
{
  printf("nftw_handler got : %s\n", fpath);
  return 0;
}

int main(int argc, char *argv[])
{
 
 if( argc == 1) 
 {
 	printf("usage %s dir\n",argv[0]);
	return 1;
 }

 if( nftw(argv[1], nftw_handler, 10, 0) == -1)
 {
 	printf("nftw error: %s\n",strerror(errno));
	return 1;
 }
 
 return 0;
}
