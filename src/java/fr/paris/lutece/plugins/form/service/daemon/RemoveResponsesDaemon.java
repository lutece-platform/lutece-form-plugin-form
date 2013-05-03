package fr.paris.lutece.plugins.form.service.daemon;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormFilter;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.business.FormSubmitHome;
import fr.paris.lutece.plugins.form.business.ResponseFilter;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.List;


/**
 * Daemon to remove every submition of the form.
 */
public class RemoveResponsesDaemon extends Daemon
{
    private static final String MESSAGE_FORM_RESPONSES_REMOVED = " responses of forms have been removed";
    private static final String MESSAGE_NO_FORM_RESPONSES_REMOVED = " responses of forms have been removed";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        List<Form> listForms = FormHome.getFormList( new FormFilter( ), plugin );
        int nNbResponses = 0;
        for ( Form form : listForms )
        {
            ResponseFilter responseFilter = new ResponseFilter( );
            responseFilter.setIdForm( form.getIdForm( ) );

            List<FormSubmit> listFormSubmit = FormSubmitHome.getFormSubmitList( responseFilter, plugin );
            for ( FormSubmit formSubmit : listFormSubmit )
            {
                FormSubmitHome.remove( formSubmit.getIdFormSubmit( ), plugin );
            }
            nNbResponses += listFormSubmit.size( );
        }
        if ( nNbResponses > 0 )
        {
            this.setLastRunLogs( nNbResponses + MESSAGE_FORM_RESPONSES_REMOVED );
        }
        else
        {
            this.setLastRunLogs( MESSAGE_NO_FORM_RESPONSES_REMOVED );
        }
    }

}
