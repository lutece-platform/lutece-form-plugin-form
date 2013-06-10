package fr.paris.lutece.plugins.form.service.daemon;

import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormHome;
import fr.paris.lutece.plugins.form.service.FormPlugin;
import fr.paris.lutece.plugins.form.service.FormService;
import fr.paris.lutece.portal.service.daemon.Daemon;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;

import java.util.List;


/**
 * Daemon to remove every submition of the form.
 */
public class RemoveResponsesDaemon extends Daemon
{
    private static final String MESSAGE_FORM_RESPONSES_REMOVED = "Responses of forms have been removed";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run( )
    {
        Plugin plugin = PluginService.getPlugin( FormPlugin.PLUGIN_NAME );
        List<Form> listForms = FormHome.getFormListForAutomaticCleaning( plugin );
        for ( Form form : listForms )
        {
            FormService.getInstance( ).cleanFormResponses( form );
        }
        this.setLastRunLogs( MESSAGE_FORM_RESPONSES_REMOVED );
    }

}
