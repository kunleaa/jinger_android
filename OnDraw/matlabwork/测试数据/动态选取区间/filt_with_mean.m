function [rol,col] = filt_with_mean(meanacc,meanori,condition)
increment_1 = meanacc - meanori;
increment_2 = increment_1.*(abs(increment_1) < condition);
[rol,col] = find(increment_2 ~= 0);
