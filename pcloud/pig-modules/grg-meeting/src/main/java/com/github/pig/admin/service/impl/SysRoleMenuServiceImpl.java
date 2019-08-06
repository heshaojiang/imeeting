package com.github.pig.admin.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pig.admin.mapper.SysRoleMenuMapper;
import com.github.pig.admin.model.entity.SysRoleMenu;
import com.github.pig.admin.service.SysRoleMenuService;
import com.xiaoleilu.hutool.collection.CollUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 角色菜单表 服务实现类
 * </p>
 *
 * @author lengleng
 * @since 2017-10-29
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

    private CacheManager cacheManager;

    private static final Logger logger = LoggerFactory.getLogger(SysRoleMenuServiceImpl.class);

    /**
     * @param role
     * @param roleId  角色
     * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
     * @return
     */
    @Override
    @CacheEvict(value = "menu_details", key = "#role + '_menu'")
    public Boolean insertRoleMenus(String role, Integer roleId, String menuIds) {
        SysRoleMenu condition = new SysRoleMenu();
        condition.setRoleId(roleId);
        this.delete(new EntityWrapper<>(condition));
        List<SysRoleMenu> roleMenuList = new ArrayList<>();
        List<String> menuIdList = Arrays.asList(menuIds.split(","));
        if (CollUtil.isEmpty(menuIdList)) {
            return Boolean.TRUE;
        }
        for (String menuId : menuIdList) {
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(Integer.valueOf(menuId));
            roleMenuList.add(roleMenu);
        }

        //清空userinfo
        try {
            cacheManager.getCache("user_details").clear();
        } catch (Exception e) {
            logger.info("SysRoleMenuServiceImpl_insertRoleMenus_clearCache_Exception"+e);
        }
        return this.insertBatch(roleMenuList);
    }
}
