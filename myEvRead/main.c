/*
 * main.c
 *
 *  Created on: 2010-11-28
 *      Author: yigiter
 */

#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

#include <unistd.h>
#include <linux/input.h>
#include <stdio.h>

#include <sys/ioctl.h>
#include "akm_header.h"

#include <errno.h>
#include <string.h>

#define MXEV	(20)

void record_dat(struct input_event *ev);

int main() {
	int inpfd;
	int ctlfd;
	struct input_event sdat[MXEV];	//Read max MXEV inputs per read
	int NBYT=sizeof(struct input_event);
	int retv;
	int i,in;	//for counters
	short buffer[20]={1,1,1,1,1,1,1}; //misc buffer



	//open input
	inpfd=open("/dev/input/event4", O_RDONLY ); //| O_NONBLOCK
	if (inpfd==-1) {
		printf("event4 cannot be opened\n");
		return -1;
	}

	//open control
	ctlfd=open("/dev/akm8973_daemon", O_RDONLY);
	if (ctlfd==-1) {
		printf("Control (aot) cannot be opened\n");
		return -1;
	}

	/*
	retv=ioctl(ctlfd, ECS_IOCTL_APP_SET_MFLAG , &buffer[0]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_SET_AFLAG , &buffer[1]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_SET_TFLAG , &buffer[2]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_SET_MVFLAG , &buffer[4]);

	retv=ioctl(ctlfd, ECS_IOCTL_APP_GET_MFLAG , &buffer[0]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_GET_AFLAG , &buffer[1]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_GET_TFLAG , &buffer[2]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_GET_DELAY , &buffer[3]);
	retv=ioctl(ctlfd, ECS_IOCTL_APP_GET_MVFLAG , &buffer[4]);
	*/

	retv=ioctl(ctlfd, ECS_IOCTL_GET_DELAY , &buffer[3]);

	if (retv<0) {
		printf("ioctl error:%d - %s\n",retv, strerror(errno));
	}
	else {
		//printf("MFLAG :%hd\n", buffer[0]);
		//printf("AFLAG :%hd\n", buffer[1]);
		//printf("TFLAG :%hd\n", buffer[2]);
		printf("DELAY :%hd\n", buffer[3]);
		//printf("MVFLAG :%hd\n", buffer[4]);
	}
	close(ctlfd);





	for (in=0;in<2;in++) {

		retv = read(inpfd, sdat, NBYT * MXEV);
		if (retv<NBYT)	{ //not even an input could be read
			printf("Error in reading:%d\n",retv);
			//return -1;
		}

		//Print the results
		for (i = 0; i < retv / NBYT; i++) {
			record_dat(&sdat[i]);
		}

	}
	close(inpfd);

	return 0;
}


void record_dat(struct input_event *ev)
{
	if (ev->type == EV_SYN) {
					printf("Event: time %ld.%06ld, -------------- %s ------------\n",
						ev->time.tv_sec, ev->time.tv_usec, ev->code ? "Config Sync" : "Report Sync" );
				} else if (ev->type == EV_MSC && (ev->code == MSC_RAW || ev->code == MSC_SCAN)) {
					printf("Event: time %ld.%06ld, type %d, code %d, value %02x\n",
						ev->time.tv_sec, ev->time.tv_usec, ev->type,
						ev->code,
						ev->value);
				} else {
					printf("Event: time %ld.%06ld, type %d, code %d, value %d\n",
						ev->time.tv_sec, ev->time.tv_usec, ev->type,
						ev->code,
						ev->value);
				}

	return;
}
