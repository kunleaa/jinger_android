function [mean_acc,mean_senser,oriout,variance_acc] = ori_dis_simulator(acc_xyz,mmindex,ori_c_o,isplot,navimodel,path)
ON = 1;
OFF = 0;

NORMAL = 1;
INDEPENDENT = 2;
CORRALETIVE = 3;

%遍历 [波峰波谷四分之一 -5 , 波峰] 这个区间 
for interval = 0:1:6
    index_mean = 1;
    for start = -5:1:5
        endd = start + interval;
        for i = 1:1:length(mmindex)
            orient(i,index_mean,interval+1) = OrientWithTime(mmindex(i,2),mmindex(i,1),acc_xyz(:,1),acc_xyz(:,2),start,endd);
        end
        
        if navimodel == NORMAL
            [position_x, position_y, oriout(:,index_mean,interval+1)] = navigate_normal(orient(:,index_mean,interval+1), acc_xyz, mmindex);
        end
        
        if navimodel == INDEPENDENT 
            [position_x, position_y, oriout(:,index_mean,interval+1)] = navigate_towstep_independent(orient(:,index_mean,interval+1), acc_xyz, mmindex);
        end
        
        if navimodel == CORRALETIVE
            [position_x, position_y, oriout(:,index_mean,interval+1)] = navigate_towstep_correlative(orient(:,index_mean,interval+1), acc_xyz, mmindex);
        end
        
        figure
        plot(position_x,position_y);
        title(['start:',int2str(start),' end:',int2str(endd)]);
        savepicture(path,get_picture_name(interval,start,endd));

        if isplot == OFF
            close(figure(gcf)) 
        end

        mean_acc(index_mean,interval+1) = mean_oriacc(oriout(:,index_mean,interval+1));
        variance_acc(index_mean,interval+1) = variance_oriacc(oriout(:,index_mean,interval+1),mean_acc(index_mean,interval+1));
        index_mean = index_mean+1;
    end
end
mean_senser = mean_oriacc(ori_c_o(:,2));

mean_senser;
mean_acc;
oriout;
variance_acc;

function [vari] = variance_oriacc(orient,mean)
vari = abs(orient-mean);
[rol,col] = find(vari>180);
if length(rol) > 0
    for i = 1 : length(rol)
        vari(rol(i),col(i)) = 360 - vari(rol(i),col(i));
    end
end

vari = sqrt(sum(vari.*vari)/length(orient));


