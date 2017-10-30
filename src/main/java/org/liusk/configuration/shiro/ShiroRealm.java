/**
 * BEYONDSOFT.COM INC
 */
package org.liusk.configuration.shiro;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.liusk.entity.Menu;
import org.liusk.entity.Role;
import org.liusk.entity.User;
import org.liusk.service.MenuService;
import org.liusk.service.RoleService;
import org.liusk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author liusk
 * @version $Id: ShiroRealm.java, v 0.1 2017年10月27日 下午3:11:05 liusk Exp $
 */
public class ShiroRealm extends AuthorizingRealm {
    private Logger logger = LoggerFactory.getLogger(ShiroRealm.class);

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private MenuService menuService;

    /**
     * 登录认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        logger.info("验证当前Subject时获取到token为：" + token.toString());
        //查出是否有此用户
        User user = userService.queryByAccount(token.getUsername());
        if (user != null) {
            //成功则放入session
            //Session session = SecurityUtils.getSubject().getSession();
            //session.setAttribute("user", user);

            // 若存在，将此用户存放到登录认证info中，无需自己做密码对比，Shiro会为我们进行密码对比校验
            AuthenticationInfo authcInfo = new SimpleAuthenticationInfo(user.getAccount(),
                user.getPassword(), getName());
            return authcInfo;
        }
        return null;
    }

    /**
     * 权限认证
     *
     * @param principalCollection
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        logger.info("##################执行Shiro权限认证##################");
        //PrincipalCollection存了权限信息的解决方案
        //User user = (User) principalCollection.getPrimaryPrincipal();

        //PrincipalCollection存登陆用户名的解决方案，
        //获取当前登录输入的用户名，等价于(String) principalCollection.fromRealm(getName()).iterator().next();
        String loginName = (String) super.getAvailablePrincipal(principalCollection);
        User user = userService.queryByAccount(loginName);
        if (user != null) {
            //权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            //用户的角色集合
            List<Role> roleList = roleService.queryByUserId(user.getUserId());
            List<String> roleStr = roleList.stream().map(Role::getRoleId)
                .collect(Collectors.toList());
            info.addRoles(roleStr);

            //用户的权限集合
            List<Menu> menus = menuService.queryByUserId(user.getUserId());
            List<String> menuStr = menus.stream().map(Menu::getMenuId).collect(Collectors.toList());
            info.addStringPermissions(menuStr);

            return info;
        }
        // 返回null的话，就会导致任何用户访问被拦截的请求时，都会自动跳转到unauthorizedUrl指定的地址
        return null;
    }
}
