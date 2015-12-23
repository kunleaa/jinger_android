clear; 
clc;
acc_xyz = importdata('data_acc.txt');
mmindex = importdata('data_min_max_index.txt');
ori_c_o = importdata('data_angel.txt');

%遍历 [波峰波谷四分之一 -5 , 波峰] 这个区间 
for interval = 0:1:6
    index_mean = 1;
    for start = -5:1:5
        endd = start + interval;
        for i = 1:1:length(mmindex)
            orient(i,index_mean) = OrientWithTime(mmindex(i,2),mmindex(i,1),acc_xyz(:,1),acc_xyz(:,2),start,endd);
        end
        mean_acc(index_mean,interval+1) = mean_oriacc(orient(:,index_mean));
        index_mean = index_mean+1;
    end
end

mean_senser = mean_oriacc(ori_c_o(:,2));

mean_senser
mean_acc
