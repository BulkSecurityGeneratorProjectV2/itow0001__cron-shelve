package org.jenkinsci.plugins.cronshelve;
import hudson.Extension;
import hudson.model.Hudson;
import hudson.model.ManagementLink;
import hudson.security.Permission;
import java.io.IOException;
import java.util.logging.Logger;
import org.kohsuke.stapler.HttpRedirect;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
/**
 * Responsible for UI management tasks passing values in and out
 */
@Extension
public class CronShelveLink extends ManagementLink{
	private static final Logger LOGGER = Logger.getLogger(CronShelveLink.class.getName());
	@Override
	public String getDisplayName() {
		return "Cron-Shelve";
	}
    @Override
    public String getDescription() {
       return " Performs job shelving actions based on cron schedule and regex";
    }

	@Override
	public String getIconFileName() {
		return "package.gif";
	}

	@Override
	public String getUrlName() {
		return "Cron-Shelve";
	}
	
	// Provides a easy means to grab this class
    public CronShelveLink getCronShelveLink() {
        return this;
    }
    
    private void checkPermission(Permission permission) {
        Hudson.getInstance().checkPermission(permission);
    }
    
    public boolean someEnabledValue() {
        return true;
    }
   
    public HttpResponse doSaveSettings(StaplerRequest res, StaplerResponse rsp,
    		@QueryParameter("enable") boolean enable,
    		@QueryParameter("cron") String cron,
    		@QueryParameter("regex") String regex,
    		@QueryParameter("days") int days) throws IOException {
      checkPermission(Hudson.ADMINISTER);
      String daysStr = Integer.toString(days);
      LOGGER.warning("values passed in cron: "+cron+" regex: "+regex+" days: "+daysStr);
      
      //update values to plugin and xml via CronShelvePluginImp
      final CronShelvePluginImp plugin =  getConfiguration(); //CronShelvePluginImp.getInstance();
      plugin.setEnable(enable);
      plugin.setCron(cron);
      plugin.setRegex(regex);
      plugin.setDays(days);
      plugin.save();
      //Updates CronExecutor with new values
      CronExecutor cronListener = getCronExecutor();
      cronListener.setEnable(enable);
      cronListener.setCron(cron);
      cronListener.setRegex(regex);
      cronListener.setDays(days);
      
      /*
	  	CronExecutor cronExec = new CronExecutor();
	  	cronExec.setCron(cron);
	  	cronExec.setRegex(regex);
	  */
      return new HttpRedirect("index");
    }
    // Provides a easy means to populate and validate values in UI via CronShelvePluginImp
    public CronShelvePluginImp getConfiguration()
    {
    	return CronShelvePluginImp.getInstance();
    }
    
    // Provides a easy means to update values in cron listener/
    public CronExecutor getCronExecutor()
    {
    	return CronExecutor.getInstance();
    }
}
