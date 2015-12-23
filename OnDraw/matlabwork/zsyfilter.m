clear all;
clc;
close all;

fs = 44100;
% x = wavread('b.wav');
t = -5*pi:pi/100:5*pi;
x = sin(t);
x = x(:);
sx = size(x,1);

subplot(4,2,1);
plot(x);axis([0 sx -1 1]);

% 原信号FFT
xf = fft(x,1024);
subplot(4,2,2);
plot(abs(xf));

% 添加高斯噪声

ex=importdata('OnDrawData3.txt');
ex=ex(100:1:end,:);
col = ex(:,1) - mean(ex(:,1));

t = 0 : 1/fs : (sx-1)/fs;
noise = 10*col(1:length(x),1);  % 均值为0，方差为0.5的标准正态噪声
subplot(4,2,3);
plot(noise);xlim([0 sx]);
subplot(4,2,4);
plot(abs(fft(noise,1024)))

x1 = x + noise;
subplot(4,2,5);
plot(x1);xlim([0 sx]);

% 信号加噪声后的FFT
xf = fft(x1,1024);
subplot(4,2,6);
plot(abs(xf));

% LMS自适应滤波
param.M        = 50;
param.w        = ones(param.M, 1) * 0.1;
param.u        = 0.1;
param.max_iter = 100;
param.min_err  = 0.5;

[yn err] = zx_lms(x1(:,1), x(:,1), param);

subplot(4,2,7)
plot(yn);xlim([0 sx]);

ynf = fft(yn(param.M:end), 1024);
subplot(4,2,8)
plot(abs(ynf));