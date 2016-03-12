function [mean_factor] = cal_mean_factor(mean_acc,mean_senser)
%with deliberation
mean_factor = abs(mean_acc-mean_senser);
[rol,col] = find(mean_factor>180);

if length(col) > 0
    for i=1:length(col)
        mean_factor(rol(i),col(i)) = 360-mean_factor(rol(i),col(i));
    end
end
maxvalue = max(max(mean_factor));
minvalue = min(min(mean_factor));
mean_factor = (mean_factor-minvalue)/(maxvalue - minvalue);
