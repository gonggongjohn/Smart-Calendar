def add_link(origin_str, target_num):
    if origin_str and origin_str != "":
        link_list = origin_str.split(',')
        link_list.append(str(target_num))
        return ','.join(link_list)
    else:
        return str(target_num)
