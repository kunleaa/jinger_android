function [name] = get_picture_name(interval,start,endd)
name = [int2str(interval),'_',int2str(start),'_',int2str(endd)];