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
public class FormJspBeanTest extends LuteceTestCase
{
    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getManageForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetManageForm(  ) throws AccessDeniedException
    {
        System.out.println( "getManageForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getManageForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getCreateForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetCreateForm(  ) throws AccessDeniedException
    {
        System.out.println( "getCreateForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getCreateForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getModifyForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetModifyForm(  ) throws AccessDeniedException
    {
        System.out.println( "getModifyForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getModifyForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getConfirmRemoveForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetConfirmRemoveForm(  ) throws AccessDeniedException
    {
        System.out.println( "getConfirmRemoveForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getConfirmRemoveForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getModifyRecap(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetModifyRecap(  ) throws AccessDeniedException
    {
        System.out.println( "getModifyRecap" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getModifyRecap( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getCreateEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetCreateEntry(  ) throws AccessDeniedException
    {
        System.out.println( "getCreateEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getCreateEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getModifyEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetModifyEntry(  ) throws AccessDeniedException
    {
        System.out.println( "getModifyEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getModifyEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getConfirmRemoveEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetConfirmRemoveEntry(  ) throws AccessDeniedException
    {
        System.out.println( "getConfirmRemoveEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getConfirmRemoveEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getMoveEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetMoveEntry(  ) throws AccessDeniedException
    {
        System.out.println( "getMoveEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getMoveEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getConfirmDisableForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetConfirmDisableForm(  ) throws AccessDeniedException
    {
        System.out.println( "getConfirmDisableForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getConfirmDisableForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getModifyField(javax.servlet.http.HttpServletRequest, boolean)}.
     */
    public void testGetModifyField(  ) throws AccessDeniedException
    {
        System.out.println( "getModifyField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        boolean bWithConditionalQuestion = true;

        String result = instance.getModifyField( request, bWithConditionalQuestion );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getConfirmRemoveField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetConfirmRemoveField(  ) throws AccessDeniedException
    {
        System.out.println( "getConfirmRemoveField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getConfirmRemoveField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getTestForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetTestForm(  ) throws AccessDeniedException
    {
        System.out.println( "getTestForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getTestForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#getResult(javax.servlet.http.HttpServletRequest)}.
     */
    public void testGetResult(  ) throws AccessDeniedException
    {
        System.out.println( "getResult" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.getResult( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doCreateForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoCreateForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoCreateForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doCreateForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doModifyForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoModifyForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoModifyForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doModifyForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doRemoveForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoRemoveForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoRemoveForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doRemoveForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doCopyForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoCopyForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoCopyForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doCopyForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doModifyRecap(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoModifyRecap(  ) throws AccessDeniedException
    {
        System.out.println( "testDoModifyRecap" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doModifyRecap( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doCreateEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoCreateEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoCreateEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doCreateEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doModifyEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoModifyEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoModifyEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doModifyEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doRemoveEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoRemoveEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoRemoveEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doRemoveEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doCopyEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoCopyEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoCopyEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doCopyEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doMoveEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoMoveEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoMoveEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doMoveEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doMoveOutEntry(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoMoveOutEntry(  ) throws AccessDeniedException
    {
        System.out.println( "testDoMoveOutEntry" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doMoveOutEntry( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doDisableForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoDisableForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoDisableForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doDisableForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doEnableForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoEnableForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoEnableForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doEnableForm( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doCreateField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoCreateField(  ) throws AccessDeniedException
    {
        System.out.println( "testDoCreateField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doCreateField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doModifyField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoModifyField(  ) throws AccessDeniedException
    {
        System.out.println( "testDoModifyField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doModifyField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doRemoveField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoRemoveField(  ) throws AccessDeniedException
    {
        System.out.println( "testDoRemoveField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doRemoveField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doMoveUpField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoMoveUpField(  ) throws AccessDeniedException
    {
        System.out.println( "testDoMoveUpField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doMoveUpField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doMoveDownField(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoMoveDownField(  ) throws AccessDeniedException
    {
        System.out.println( "testDoMoveDownField" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doMoveDownField( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doRemoveRegularExpression(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoRemoveRegularExpression(  ) throws AccessDeniedException
    {
        System.out.println( "testDoRemoveRegularExpression" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doRemoveRegularExpression( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doInsertRegularExpression(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoInsertRegularExpression(  ) throws AccessDeniedException
    {
        System.out.println( "testDoInsertRegularExpression" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doInsertRegularExpression( request );

        assertNotNull( result );
    }

    /**
     * Test method for {@link fr.paris.lutece.plugins.form.web.FormJspBean#doTestForm(javax.servlet.http.HttpServletRequest)}.
     */
    public void testDoTestForm(  ) throws AccessDeniedException
    {
        System.out.println( "testDoTestForm" );

        MokeHttpServletRequest request = new MokeHttpServletRequest(  );

        AdminUser user = AdminUserHome.findUserByLogin( "admin" );
        user.setRoles( AdminUserHome.getRolesListForUser( user.getUserId(  ) ) );
        request.registerAdminUserWithRigth( user, FormJspBean.RIGHT_MANAGE_FORM );

        FormJspBean instance = new FormJspBean(  );

        instance.init( request, FormJspBean.RIGHT_MANAGE_FORM );

        String result = instance.doTestForm( request );

        assertNotNull( result );
    }
}
