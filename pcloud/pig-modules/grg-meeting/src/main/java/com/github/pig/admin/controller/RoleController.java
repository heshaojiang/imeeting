package com.github.pig.admin.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pig.admin.model.dto.RoleDto;
import com.github.pig.admin.model.entity.SysRole;
import com.github.pig.admin.service.SysRoleMenuService;
import com.github.pig.admin.service.SysRoleService;
import com.github.pig.common.constant.CommonConstant;
import com.github.pig.common.util.Query;
import com.github.pig.common.util.R;
import com.github.pig.common.util.UserUtils;
import com.github.pig.common.web.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author lengleng
 * @date 2017/11/5
 */
@RestController
@RequestMapping("/role")
@Slf4j
public class RoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysRoleMenuService sysRoleMenuService;

    /**
     * 通过ID查询角色信息(pig)
     *
     * @param id ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public SysRole role(@PathVariable Integer id) {
        return sysRoleService.selectById(id);
    }

    /**
     * 添加角色(pig)
     *
     * @param roleDto 角色信息
     * @return success、false
     */
    @PostMapping
    public R<Boolean> role(@RequestBody RoleDto roleDto) {
        return new R<>(sysRoleService.insertRole(roleDto));
    }

    /**
     * 添加角色(admin)
     *
     * @param sysRole 角色信息
     * @return success、false
     */
    @PostMapping("/admin")
    public R<Boolean> adminRole(@RequestBody SysRole sysRole) {
        return new R<>(sysRoleService.insert(sysRole));
    }


    /**
     * 添加角色(imeeting)
     * 注：目前来看dashboard是没有添加角色及修改的权限
     *
     * @param sysRole 角色信息
     * @return success、false
     */
    @PostMapping("imeeting")
    public R<Boolean> imeetingRole(@RequestBody SysRole sysRole) {
        return new R<>(sysRoleService.insert(sysRole));
    }

    /**
     * 修改角色
     *
     * @param roleDto 角色信息
     * @return success/false
     */
    @PutMapping
    public R<Boolean> roleUpdate(@RequestBody RoleDto roleDto) {
        return new R<>(sysRoleService.updateRoleById(roleDto));
    }

    @DeleteMapping("/{id}")
    public R<Boolean> roleDel(@PathVariable Integer id) {
        SysRole sysRole = sysRoleService.selectById(id);
        sysRole.setDelFlag(CommonConstant.STATUS_DEL);
        return new R<>(sysRoleService.updateById(sysRole));
    }

    /**
     * 获取角色列表
     *
     * @param deptId  部门ID
     * @return 角色列表
     */
    @GetMapping("/roleList/{deptId}")
    public List<SysRole> roleList(@PathVariable Integer deptId) {
        return sysRoleService.selectListByDeptId(deptId);
    }

    /**
     * 分页查询角色信息(pig)
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @RequestMapping("/rolePage")
    public Page rolePage(@RequestParam Map<String, Object> params) {
        params.put(CommonConstant.DEL_FLAG, CommonConstant.STATUS_NORMAL);
        return sysRoleService.selectwithDeptPage(new Query<>(params), new EntityWrapper<>());
    }


    /**
     * 分页查询角色信息(admin)
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @RequestMapping("/sysRolePage")
    public Page sysRolePage(@RequestParam Map<String, Object> params) {
        return sysRoleService.selectSysRolePage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 获取角色列表
     *
     * @param
     * @return
     */
    @RequestMapping("/getSysRoleList/{locale}")
    public R<List<SysRole>> getSysRoleList(@PathVariable String locale) {
        String userName = UserUtils.getUser();
        String level = sysRoleService.getSysRoleLevel(userName);

        log.info("-------getSysRoleList(): locale = {},username = {},level = {} ------------", locale, userName, level);
        List<SysRole> sysRoleList = sysRoleService.selectList(new EntityWrapper<SysRole>().gt("role_level", level));
        if (CommonConstant.ENUS.equals(locale)) {
            for (SysRole sysRole : sysRoleList) {
                String roleCode = sysRole.getRoleCode();
                sysRole.setRoleName(roleCode);
            }
        }
        return new R<>(true, sysRoleList);
    }


    /**
     * 分页查询角色信息(imeeting)
     *
     * @param params 分页对象
     * @return 分页对象
     */
    @RequestMapping("/imeetingRolePage")
    public Page imeetingRolePage(@RequestParam Map<String, Object> params) {
        return sysRoleService.selectImeetingRolePage(new Query<>(params), new EntityWrapper<>());
    }

    /**
     * 更新角色菜单
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID拼成的字符串，每个id之间根据逗号分隔
     * @return success、false
     */
    @PutMapping("/roleMenuUpd")
    public R<Boolean> roleMenuUpd(Integer roleId, @RequestParam(value = "menuIds", required = false) String menuIds) {
        SysRole sysRole = sysRoleService.selectById(roleId);
        return new R<>(sysRoleMenuService.insertRoleMenus(sysRole.getRoleCode(), roleId, menuIds));
    }
}
