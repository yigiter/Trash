/*
 * main.c
 *
 *  Created on: 2010-12-09
 *      Author: yigiter
 */
#include <math.h>
#include <stdlib.h>
#include <stdio.h>

#define CHSIZE 	15
#define pi 		(3.14159265)
#define MTHRESH	(0.001)


int fit_circle(float cache[][CHSIZE], float *cparam);
int comp_3x3inv(float A[][3], float Ainv[][3]);

int main(){
	float cache[3][CHSIZE];
	int i;
	float rad=5;
	float centre[2]={3,7};
	float cparam[3]; //centre x, centre y, rad

	float teta=15*pi/180;
	for (i=0;i<CHSIZE;i++) {
		cache[0][i]=centre[0]+rad*cos(teta);
		cache[1][i]=centre[1]+rad*sin(teta);
		teta=teta+7*pi/180;
	}

	fit_circle(cache, cparam);

	return 0;
}



int fit_circle(float cache[][CHSIZE], float *cparam) {
	//Kasa's method

	int i,k,m;
	float psmat[3][3];
	float psmat_inv[3][3];
	float y[CHSIZE];
	float vr_a[3],vr_b[3];
	int rv;


	//Replace 3rd row with ones
	for (i=0;i<CHSIZE;i++)
		cache[2][i]=1;

	//pseudo matrix (A'A)
	for (i=0;i<3;i++)
		for (k=0;k<3;k++) {
			psmat[i][k]=0;
			for (m=0;m<CHSIZE;m++)
				psmat[i][k]=psmat[i][k]+cache[i][m]*cache[k][m];
		}

	//y
	for (i=0;i<CHSIZE;i++)
		y[i]=cache[0][i]*cache[0][i]+cache[1][i]*cache[1][i];

	//vr_a=A'*y
	for (i=0;i<3;i++) {
		vr_a[i]=0;
		for (k=0;k<CHSIZE;k++)
			vr_a[i]=vr_a[i]+cache[i][k]*y[k];
	}

	//Pseudo inverse matrix
	rv=comp_3x3inv(psmat, psmat_inv);

	if (rv)
		//psmat is singular.
		return 1;

	//result=inv(A'A)*A'y;
	for (i=0;i<3;i++) {
		vr_b[i]=0;
		for (k=0;k<3;k++)
			vr_b[i]=vr_b[i]+psmat_inv[i][k]*vr_a[k];
	}

	//Convert result to the circular parameters
	cparam[0]=vr_b[0]/2;	//x bias
	cparam[1]=vr_b[1]/2;	//y bias
	cparam[2]=sqrt((vr_b[0]*vr_b[0]+vr_b[1]*vr_b[1])/4.0+vr_b[2]);	//Radius

	return 0;
}

int comp_3x3inv(float A[][3], float Ainv[][3]) {
	//hard coded 3x3 inverse
	float det=0;

	det=A[0][0]*A[1][1]*A[2][2];
	det=det+A[0][1]*A[1][2]*A[2][0];
	det=det+A[0][2]*A[1][0]*A[2][1];
	det=det-A[0][2]*A[1][1]*A[2][0];
	det=det-A[0][1]*A[1][0]*A[2][2];
	det=det-A[0][0]*A[1][2]*A[2][1];

	if (abs(det)<MTHRESH)
		return 1; //matrix is singular


	float detinv=1.0/det;

	Ainv[0][0]=(A[1][1]*A[2][2]-A[2][1]*A[1][2])*detinv;
	Ainv[0][1]=-(A[1][0]*A[2][2]-A[1][2]*A[2][0])*detinv;
	Ainv[0][2]=(A[1][0]*A[2][1]-A[2][0]*A[1][1])*detinv;
	Ainv[1][0]=-(A[0][1]*A[2][2]-A[0][2]*A[2][1])*detinv;
	Ainv[1][1]=(A[0][0]*A[2][2]-A[0][2]*A[2][0])*detinv;
	Ainv[1][2]=-(A[0][0]*A[2][1]-A[2][0]*A[0][1])*detinv;
	Ainv[2][0]=(A[0][1]*A[1][2]-A[0][2]*A[1][1])*detinv;
	Ainv[2][1]=-(A[0][0]*A[1][2]-A[1][0]*A[0][2])*detinv;
	Ainv[2][2]=(A[0][0]*A[1][1]-A[1][0]*A[0][1])*detinv;

	return 0;
}
