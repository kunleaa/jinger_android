clear all;
clc;
close all;

grid on;
tempdata=importdata('OnDrawData3ÕýÄÏ11.txt');
plot(tempdata(:,1),tempdata(:,2))
axis([-1.5 1.5 -1.5 1.5]);
plot3([1:length(tempdata(:,1))],tempdata(:,1),tempdata(:,2))
axis([-1.5 1.5 -1.5 1.5]);
axis([1 length(tempdata(:,1)) -1.5 1.5 -1.5 1.5]);
plot3([1:length(tempdata(:,1))],tempdata(:,1),tempdata(:,2))
axis([1 length(tempdata(:,1)) -1.5 1.5 -1.5 1.5]);
figure
plot(tempdata)
figure
plot(tempdata(:,1),tempdata(:,2))
axis([-1.5 1.5 -1.5 1.5]);
figure
plot(tempdata(:,1))
figure
plot(tempdata(:,2))

ex=importdata('OutData.txt');
plot(ex);xlim([1 1001])

