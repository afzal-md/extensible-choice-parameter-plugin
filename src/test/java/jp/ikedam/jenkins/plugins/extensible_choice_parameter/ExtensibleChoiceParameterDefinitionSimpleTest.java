/*
 * The MIT License
 * 
 * Copyright (c) 2012-2013 IKEDA Yasuyuki
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

import static org.junit.Assert.*;

import hudson.model.StringParameterValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * Test for ExtensibleChoiceParameterDefinition, not corresponding to Jenkins.
 */
public class ExtensibleChoiceParameterDefinitionSimpleTest
{
    @Test
    public void testExtensibleChoiceParameterDefinition_name()
    {
        // Simple value
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    false,
                    "Some text"
            );
            assertEquals("Simple value", "name", target.getName());
        }
        // value surrounded with spaces.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "  name ",
                    null,
                    false,
                    "Some text"
            );
            assertEquals("value surrounded with spaces.", "name", target.getName());
        }
    }
    
    @Test
    public void testExtensibleChoiceParameterDefinition_nameWithInvalidValue()
    {
        // null
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    null,
                    null,
                    false,
                    "Some text"
            );
            assertEquals("null", null, target.getName());
        }
        // empty
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "",
                    null,
                    false,
                    "Some text"
            );
            assertEquals("Empty", "", target.getName());
        }
        // blank.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "  ",
                    null,
                    false,
                    "Some text"
            );
            assertEquals("blank", "", target.getName());
        }
    }
    
    @Test
    public void testExtensibleChoiceParameterDefinition_description()
    {
        // Simple value
        {
            String description = "Some text";
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    false,
                    description
            );
            assertEquals("Simple value", description, target.getDescription());
        }
        
        // value surrounded with blank letters
        {
            String description = " \nSome\n text ";
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    false,
                    description
            );
            assertEquals("value surrounded with blank letters", description, target.getDescription());
        }
        
        // null
        {
            String description = null;
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    false,
                    description
            );
            assertEquals("null", description, target.getDescription());
        }
        
        // empty
        {
            String description = "";
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    false,
                    description
            );
            assertEquals("empty", description, target.getDescription());
        }
    }
    
    
    @Test
    public void testExtensibleChoiceParameterDefinition_choiceListProvider()
    {
        // Simple value
        {
            ChoiceListProvider provider = new TextareaChoiceListProvider("a\nb\nc\n", null, false, null);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    provider,
                    false,
                    "Some Text"
            );
            assertEquals("Simple value", provider, target.getChoiceListProvider());
        }
        
        // null
        {
            ChoiceListProvider provider = null;
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    provider,
                    false,
                    "Some Text"
            );
            assertEquals("null", provider, target.getChoiceListProvider());
        }
    }
    
    @Test
    public void testExtensibleChoiceParameterDefinition_editable()
    {
        // editable
        {
            boolean editable = true;
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    editable,
                    "Some Text"
            );
            assertEquals("editable", editable, target.isEditable());
        }
        
        // noneditable
        {
            boolean editable = false;
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    "name",
                    null,
                    editable,
                    "Some Text"
            );
            assertEquals("noneditable", editable, target.isEditable());
        }
    }
    
    private static class MockChoiceListProvider extends ChoiceListProvider
    {
        private List<String> choiceList = null;
        private String defaultChoice = null;
        public MockChoiceListProvider(List<String> choiceList, String defaultChoice){
            this.choiceList = choiceList;
            this.defaultChoice = defaultChoice;
        }
        @Override
        public List<String> getChoiceList()
        {
            return choiceList;
        }
        
        @Override
        public String getDefaultChoice()
        {
            return defaultChoice;
        }
        
    }
    
    // Test for createValue(String value)
    @Test
    public void testCreateValueForCli()
    {
        String name = "name";
        String description = "Some text";
        ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), null);
        
        // select with editable
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            String value = "value3";
            assertEquals("select with editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // select with non-editable
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            String value = "value2";
            assertEquals("select with non-editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // input with editable
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            String value = "someValue";
            assertEquals("input with editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // input with non-editable. causes exception.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            String value = "someValue";
            try{
                assertEquals("input with non-editable", new StringParameterValue(name, value, description), target.createValue(value));
                assertTrue("input with non-editable: Code would not be reached, for an exception was triggered.", false);
            }catch(IllegalArgumentException e){
                assertTrue(true);
            }
        }
        
        // not trimmed.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            String value = "  a b\nc d e  ";
            assertEquals("not trimmed", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // provider is null and non-editable. always throw exception.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    null,
                    false,
                    description
            );
            String value = "anyValue";
            try{
                assertEquals("provider is null and non-editable", new StringParameterValue(name, value, description), target.createValue(value));
                assertTrue("input with non-editable: Code would not be reached, for an exception was triggered.", false);
            }catch(IllegalArgumentException e){
                assertTrue(true);
            }
        }
        
        // provider is null and editable. any values can be accepted.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    null,
                    true,
                    description
            );
            String value = "anyValue";
            assertEquals("provider is null and editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // no choice is provided and non-editable. always throw exception.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    false,
                    description
            );
            String value = "anyValue";
            try{
                assertEquals("no choice is provided and non-editable", new StringParameterValue(name, value, description), target.createValue(value));
                assertTrue("input with non-editable: Code would not be reached, for an exception was triggered.", false);
            }catch(IllegalArgumentException e){
                assertTrue(true);
            }
        }
        
        // no choice is provided and editable. any values can be accepted.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    true,
                    description
            );
            String value = "anyValue";
            assertEquals("no choice is provided and editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
        
        // provider returns null non-editable. always throw exception.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(null, null),
                    false,
                    description
            );
            String value = "anyValue";
            try{
                assertEquals("provider returns null and non-editable", new StringParameterValue(name, value, description), target.createValue(value));
                assertTrue("input with non-editable: Code would not be reached, for an exception was triggered.", false);
            }catch(IllegalArgumentException e){
                assertTrue(true);
            }
        }
        
        // provider returns null and editable. any values can be accepted.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(null, null),
                    true,
                    description
            );
            String value = "anyValue";
            assertEquals("provider returns null and editable", new StringParameterValue(name, value, description), target.createValue(value));
        }
    }
    
    @Test
    public void testGetDefaultParameterValue_NoDefaultChoice()
    {
        String name = "name";
        String description = "Some text";
        ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), null);
        String firstValue = "value1";
        
        // Editable
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            assertEquals("Editable", new StringParameterValue(name, firstValue, description), target.getDefaultParameterValue());
        }
        
        // Non-editable
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            assertEquals("Editable", new StringParameterValue(name, firstValue, description), target.getDefaultParameterValue());
        }
        
        // provider is null and non-editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    null,
                    false,
                    description
            );
            assertEquals("provider is null and non-editable", null, target.getDefaultParameterValue());
        }
        
        // provider is null and editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    null,
                    true,
                    description
            );
            assertEquals("provider is null and editable", null, target.getDefaultParameterValue());
        }
        
        // no choice is provided and non-editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    false,
                    description
            );
            assertEquals("no choice is provided and non-editable", null, target.getDefaultParameterValue());
        }
        
        // no choice is provided and editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    true,
                    description
            );
            assertEquals("no choice is provided and editable", null, target.getDefaultParameterValue());
        }
        
        // provider returns null non-editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(null, null),
                    false,
                    description
            );
            assertEquals("provider returns null and non-editable", null, target.getDefaultParameterValue());
        }
        
        // provider returns null and editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(null, null),
                    true,
                    description
            );
            assertEquals("provider returns null and editable", null, target.getDefaultParameterValue());
        }
    }
    
    @Test
    public void testGetDefaultParameterValue_SpecifiedDefaultChoice()
    {
        String name = "name";
        String description = "Some text";
        
        // Editable, in choices
        {
            String defaultChoice = "value2";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            assertEquals("Editable, in choices", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Non-editable, in choices
        {
            String defaultChoice = "value2";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            assertEquals("Non-Editable, in choices", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Editable, in choices, the first
        {
            String defaultChoice = "value1";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            assertEquals("Editable, in choices, the first", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Non-editable, in choices, the first
        {
            String defaultChoice = "value1";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            assertEquals("Non-Editable, in choices, the first", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Editable, in choices, the last
        {
            String defaultChoice = "value3";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            assertEquals("Editable, in choices, the last", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Non-editable, in choices, the last
        {
            String defaultChoice = "value3";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            assertEquals("Non-Editable, in choices, the last", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Editable, not in choices
        {
            String defaultChoice = "value4";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    true,
                    description
            );
            assertEquals("Editable, in choices", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
        }
        
        // Non-editable, not in choices
        {
            String defaultChoice = "value4";
            ChoiceListProvider provider = new MockChoiceListProvider(Arrays.asList("value1", "value2", "value3"), defaultChoice);
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    provider,
                    false,
                    description
            );
            try
            {
                assertEquals("Non-Editable, not in choices", new StringParameterValue(name, defaultChoice, description), target.getDefaultParameterValue());
                assertTrue("Not reachable", false);
            }
            catch(IllegalArgumentException e)
            {
                assertTrue("Non-Editable, not in choices", true);
            }
        }
        
        // no choice is provided and non-editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    false,
                    description
            );
            assertEquals("no choice is provided and non-editable", null, target.getDefaultParameterValue());
        }
        
        // no choice is provided and editable. returns null.
        {
            ExtensibleChoiceParameterDefinition target = new ExtensibleChoiceParameterDefinition(
                    name,
                    new MockChoiceListProvider(new ArrayList<String>(0), null),
                    true,
                    description
            );
            assertEquals("no choice is provided and editable", null, target.getDefaultParameterValue());
        }
    }
}
