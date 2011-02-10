/**
 *
 */
package fr.paris.lutece.plugins.form.web;

import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.business.user.AdminUserHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.MokeHttpServletRequest;


/**
 * @author jSteve
 *
 */
public class ManageFormJspBeanTest extends LuteceTestCase
{
    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.ManageFormJspBean#getManageForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetManageForm(  ) throws AccessDeniedException
    {
        System.out.println( "getManageForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, ManageFormJspBean.RIGHT_MANAGE_FORM );

        ManageFormJspBean instance = new ManageFormJspBean(  );

        instance.init( request, ManageFormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getManageForm( request );

        assertNotNull( result );
    }
}
