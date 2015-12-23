clear;
clc;
ex=importdata('OnDrawData3.txt');

ex=ex(100:1:end,:);

col_1 = ex(:,1)' - mean(ex(:,1)');

subplot(3,2,1);
plot(col_1);
std1 = std(col_1)
fft1 = fft(col_1);
fft1 = fft1(2:end);
subplot(3,2,2);
plot(abs(fft1));

col_2 = ex(:,2)' - mean(ex(:,2)');
subplot(3,2,3);
plot(col_2);
std2 = std(col_2)
fft2 = fft(col_2);
fft2 = fft2(2:end);
subplot(3,2,4);
plot(abs(fft2));

col_3 = ex(:,3)' - mean(ex(:,3)');
subplot(3,2,5);
plot(col_3);
std3 = std(col_3)
fft3 = fft(col_3);
fft3 = fft3(2:end);
subplot(3,2,6);
plot(abs(fft3));
