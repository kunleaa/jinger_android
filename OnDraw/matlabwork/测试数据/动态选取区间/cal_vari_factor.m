function [vari_factor] = cal_vari_factor(variance)
minvalue = min(min(variance));
maxvalue = max(max(variance));
vari_factor = (variance-minvalue)/(maxvalue-minvalue);