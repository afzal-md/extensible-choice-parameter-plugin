/*
 * The MIT License
 * 
 * Copyright (c) 2013 Michael Rumpf
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package jp.ikedam.jenkins.plugins.extensible_choice_parameter;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import hudson.Extension;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import org.apache.commons.lang.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * A choice provider whose choices are determined by a Groovy script.
 */
public class SystemGroovyChoiceListProvider extends ChoiceListProvider implements Serializable
{
    private static final long serialVersionUID = 2L;
    private static final String NoDefaultChoice = "###NODEFAULTCHOICE###";
    private static final Logger LOGGER = Logger.getLogger(SystemGroovyChoiceListProvider.class.getName());
    
    /**
     * The internal class to work with views.
     * 
     * The following files are used (put in main/resource directory in the source tree).
     * <dl>
     *     <dt>config.jelly</dt>
     *         <dd>
     *             Shown as a part of a job configuration page when this provider is selected.
     *             Provides additional configuration fields of a Extensible Choice.
     *         </dd>
     * </dl>
     */
    @Extension
    public static class DescriptorImpl extends Descriptor<ChoiceListProvider>
    {
        /**
         * the display name shown in the dropdown to select a choice provider.
         * 
         * @return display name
         * @see hudson.model.Descriptor#getDisplayName()
         */
        @Override
        public String getDisplayName()
        {
            return Messages._SystemGroovyChoiceListProvider_DisplayName().toString();
        }
        
        /**
         * Returns the selection of a default choice.
         * 
         * @param choiceListText
         * @return the selection of a default choice
         */
        public ListBoxModel doFillDefaultChoiceItems(@QueryParameter String scriptText)
        {
            ListBoxModel ret = new ListBoxModel();
            ret.add(Messages.ExtensibleChoiceParameterDefinition_NoDefaultChoice(), NoDefaultChoice);
            
            List<String> choices = null;
            try
            {
                choices = runScript(scriptText);
            }
            catch(Exception e)
            {
                LOGGER.log(Level.WARNING, "Failed to execute script", e);
            }
            
            if(choices != null)
            {
                for(String choice: choices)
                {
                    ret.add(choice);
                }
            }
            
            return ret;
        }
        
        public FormValidation doTest(@QueryParameter String scriptText)
        {
            List<String> choices = null;
            try
            {
                choices = runScript(scriptText);
            }
            catch(Exception e)
            {
                return FormValidation.error(e, "Failed to execute script");
            }
            
            if(choices == null)
            {
                return FormValidation.error("Script returned null.");
            }
            
            return FormValidation.ok(StringUtils.join(choices, '\n'));
        }
    }
    
    /**
     * Returns the list of choices the user specified in the job configuration page.
     * 
     * @return the list of choices.
     * @see jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider#getChoiceList()
     */
    @Override
    public List<String> getChoiceList()
    {
        List<String> ret = null;
        try
        {
            ret = runScript(getScriptText());
        }
        catch(Exception e)
        {
            LOGGER.log(Level.WARNING, "Failed to execute script", e);
        }
        return (ret != null)?ret:new ArrayList<String>(0);
    }

    private static List<String> runScript(String scriptText) {
        CompilerConfiguration compilerConfig = new CompilerConfiguration();

        // see RemotingDiagnostics.Script
        ClassLoader cl = Jenkins.getInstance().getPluginManager().uberClassLoader;

        if (cl == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }

        GroovyShell shell =
            new GroovyShell(cl, new Binding(), compilerConfig);

        Object out = shell.evaluate(scriptText);
        if(out == null)
        {
            return null;
        }
        
        if  (!(out instanceof List<?>)) {
            throw new IllegalArgumentException("Return type of the Groovy script mus be List<String>");
        }
        
        List<String> ret = new ArrayList<String>();
        for (Object obj : (List<?>) out) {
            if(obj != null) {
                ret.add(obj.toString());
            }
        }
        return ret;
    }
    private String scriptText;

    /**
     * The list of choices, joined into a string.
     * 
     * Used for filling a field when the configuration page is shown.
     * 
     * @return Joined choices.
     */
    public String getScriptText()
    {
        return scriptText;
    }
    
    private String defaultChoice = null;
    
    /**
     * Returns the default choice.
     * 
     * @return the default choice
     * @see jp.ikedam.jenkins.plugins.extensible_choice_parameter.ChoiceListProvider#getDefaultChoice()
     */
    @Override
    public String getDefaultChoice()
    {
        return defaultChoice;
    }
    
    /**
     * Constructor instantiating with parameters in the configuration page.
     * 
     * When instantiating from the saved configuration,
     * the object is directly serialized with XStream,
     * and no constructor is used.
     * 
     * @param scriptText the text where choices are written in each line.
     */
    @DataBoundConstructor
    public SystemGroovyChoiceListProvider(String scriptText, String defaultChoice)
    {
        this.scriptText = scriptText;
        this.defaultChoice = (!NoDefaultChoice.equals(defaultChoice))?defaultChoice:null;
    }
}
