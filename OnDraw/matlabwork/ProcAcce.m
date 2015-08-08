clear;
clc;
ex=importdata('AbsAccelerate.txt');

%ex=ex(100:1:end,:);

col_1 = ex(:,1)';
figure
plot(col_1);
%std1 = std(col_1)


col_2 = ex(:,2)';
figure
plot(col_2);
%std2 = std(col_2)


%col_3 = ex(:,3)';
%figure
%plot(col_3);
%std3 = std(col_3)

Speed_X = 0;
Distance_X = 0;

Speed_Y = 0;
Distance_Y = 0;

for i = 1:1:length(col_1)
    Acc_X = col_1(i);
    Distance_X = [Distance_X, Distance_X(i) + (Speed_X(i)*0.02 + Acc_X * 0.02*0.02/2)];
    Speed_X = [Speed_X, (Speed_X(i) + Acc_X*0.02)];
    
    Acc_Y = col_2(i);
    Distance_Y = [Distance_Y, Distance_Y(i) + (Speed_Y(i)*0.02 + Acc_Y * 0.02*0.02/2)];
    Speed_Y = [Speed_Y, (Speed_Y(i) + Acc_Y*0.02)];
end
figure
plot(Speed_X);
figure
plot(Speed_Y);
figure
plot(Distance_X);
figure
plot(Distance_Y);
figure
plot(Distance_X,Distance_Y);
