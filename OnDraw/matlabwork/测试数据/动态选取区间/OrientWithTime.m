function [orintation] = orientwithtime(maxindex,minindex,acc_x,acc_y,start,endd) 
X = 1;
Y = 2;
index_1_4 = floor(((maxindex+minindex)/2+maxindex)/2);
%与android程序保持一致，所以-1
start = index_1_4+start-1;
endd = index_1_4+endd-1;
acc(X) = mean(acc_x(start:endd));
acc(Y) = mean(acc_y(start:endd));
if acc(Y) <= 0 & acc(X) <= 0
    orintation = -acc(Y)/sqrt((acc(Y)^2) + (acc(X)^2));
    orintation = rad2deg(asin(orintation));
elseif acc(X) >= 0
    orintation = acc(Y)/sqrt((acc(Y)^2) + (acc(X)^2));
    orintation = rad2deg(pi + asin(orintation));
elseif acc(Y) >= 0 & acc(X) <= 0 
    orintation = acc(Y)/sqrt((acc(Y)^2 + acc(X)^2));
    orintation = rad2deg(pi*3/2 + asin(orintation));
end