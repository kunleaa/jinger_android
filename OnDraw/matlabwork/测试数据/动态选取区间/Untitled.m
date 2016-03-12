%close all
clear; 
clc;
ON = 1;
OFF = 0;

NORMAL = 1;
INDEPENDENT = 2;
CORRALETIVE = 3;

isplot = ON;
navimodel = INDEPENDENT;

path = 'E:\MATLAB7\work\picture_data';

condition_mean = 10;

acc_xyz_es = importdata('东南方向\data_acc.txt');
mmindex_es = importdata('东南方向\data_min_max_index.txt');
ori_c_o_es = importdata('东南方向\data_angel.txt');
[mean_acc_es,mean_senser_es,orient_es,variance_acc_es] = ori_dis_simulator(acc_xyz_es,mmindex_es,ori_c_o_es,isplot,navimodel,[path,'\es']);

%calculate mean factor : mf = (mean_acc - mean_sen)/maxmargin_acc_sen_ori
mean_factor_es = cal_mean_factor(mean_acc_es,mean_senser_es);
%calculate variance factor : vf = vari_acc/maxvari
variance_factor_es = cal_vari_factor(variance_acc_es);
wm = 0.7;
wv = 1 - wm;
factor_es = wm * mean_factor_es + wv * variance_factor_es

[r,c] = find(factor_es == min(min(factor_es)))

%[rol_es,col_es] = filt_with_mean(mean_acc_es,mean_senser_es,condition_mean);
%selected_section_es = trans_rolcol_to_startendd(rol_es,col_es);
%openpicture(path,'\es',selected_section_es);

acc_xyz_wn = importdata('西北方向\data_acc.txt');
mmindex_wn = importdata('西北方向\data_min_max_index.txt');
ori_c_o_wn = importdata('西北方向\data_angel.txt');
[mean_acc_wn,mean_senser_wn,orient_wn,variance_acc_wn] = ori_dis_simulator(acc_xyz_wn,mmindex_wn,ori_c_o_wn,isplot,navimodel,[path,'\wn']);

%calculate mean factor : mf = (mean_acc - mean_sen)/maxmargin_acc_sen_ori
mean_factor_wn = cal_mean_factor(mean_acc_wn,mean_senser_wn);
%calculate variance factor : vf = vari_acc/maxvari
variance_factor_wn = cal_vari_factor(variance_acc_wn);
wm = 0.5;
wv = 1 - wm;
factor_wn = wm * mean_factor_wn + wv * variance_factor_wn

%[rol_wn,col_wn] = filt_with_mean(mean_acc_wn,mean_senser_wn,condition_mean);
%selected_section_wn = trans_rolcol_to_startendd(rol_wn,col_wn);
%openpicture(path,'\wn',selected_section_wn);

%[rol,col] = filt_with_mean((mean_acc_es - mean_acc_wn),(mean_senser_es - mean_senser_wn),condition_mean);
%selected_section = trans_rolcol_to_startendd(rol,col);
%openpicture(path,'\es',selected_section);
%openpicture(path,'\wn',selected_section);