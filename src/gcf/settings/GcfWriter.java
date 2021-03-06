/*
  Settings 
  Copyright 2015 micama

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package gcf.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Writes the content of the data buffer in a formatted gcf file format.
 * All comments are lost since the are not saved in any form.
 */
class GcfWriter {
    
    /** Reference to the data buffer */
    private final Buffer buffer;
    
    /** Reference the file to be written */
    private final File file;

    
    /** Creates a SettingsFileWriter object
     * 
     * @param outputFile File object with the file to be written
     */
    public GcfWriter(final File outputFile,final Buffer buffer) {
        this.file   = outputFile;
        this.buffer = buffer;
    }

    /**
     * Writes the formatted cfg file from the inputed group information.
     */
    public void writeFile() throws GcfException
    {
        final Group topGroup = this.buffer.getGroup("/");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file,false))) {
            writeGroup(writer, topGroup, 0);
        } catch (IOException ex) {
            throw new GcfException(
                    "*** error ocurred while saving " + 
                    this.file.getAbsolutePath() + " ***");
        }
    }

    /**
     * Recursive routine to write one group with all its child keys and groups
     * @param writer reference to the file writer
     * @param group current group name
     * @param level denominator for at which group level the file currently is
     */
    public void writeGroup(
            final BufferedWriter writer, 
            final Group group, 
            final int level)
    {   
        final String prefix       = linePrefix(level);        
        final String absGroupName = group.getPath();

        if (absGroupName.equals("/")) {
            writeSubGroups(writer, group, level);
        }
        else {            
            final String groupName = group.getName();
            try {
                writeGroupHead(writer, groupName, prefix);
                writeKeys(writer, group, prefix);
                writeSubGroups(writer, group, level+1);
                writeGroupFoot(writer, groupName, prefix);
            } catch (IOException ex) {
                throw new GcfException("Problem writing group: " + groupName);
            }
        }
    }
    
    /**
     * Writes the begin of the group.
     * @param writer reference to the filewriter
     * @param groupName the current group name
     * @param prefix the prefix of white spaces for the current line
     * @throws IOException 
     */
    private void writeGroupHead(
            final BufferedWriter writer,
            final String groupName,
            final String prefix) throws IOException {
        writer.write(prefix + "[" + groupName + "]\n");
    }
    
    /**
     * writes the end of the group.
     * @param writer reference to the filewriter
     * @param groupName the current group name
     * @param prefix the prefix of white spaces for the current line
     * @throws IOException 
     */
    private void writeGroupFoot(
            final BufferedWriter writer,
            final String groupName,
            final String prefix) throws IOException {
        writer.write(prefix + "[/" + groupName + "]\n");
    }
    
    /**
     * Writes the keys of the current group.
     * @param writer reference to the filewriter
     * @param group current group object
     * @param prefix the prefix of white spaces for the current line
     * @throws IOException 
     */
    private void writeKeys(
            final BufferedWriter writer,
            final Group group,
            final String prefix) throws IOException {
        for (String key : group.childKeys()) {
            writer.write("    " + prefix + key + 
                         " = " + 
                         getStringRepresentation(group.readValue(key)) + 
                         "\n");
        }
    }
    
    /**
     * Writes the sub groups of the current group
     * @param writer reference to the filewriter
     * @param group current group
     * @param absGroupName the name of the current group
     * @param level the prefix of white spaces for the current line
     */
    private void writeSubGroups(
            final BufferedWriter writer,
            final Group group,
            final int level
            ) {
        group.childGroups().forEach(g -> writeGroup(writer, g, level));
    }

    /**
     * Gets the amounth of whitespaces at the beginning of each line 
     * @param level denominator for at which group level the file writer is
     * @return amount of white spaces with respect to the input level
     */
    public String linePrefix(final int level)
    {
        if (level == 0) {
            return "";
        }
        String prefix = "";
        for (int i = 0; i < level; i++) {
            prefix = prefix + "    ";
        }
        return prefix;
    }
    
    /**
     * Gets the string representation of a value object.<br>
     * Strings are put inbetween double quotes and all other
     * data types are just simply passed to string.
     * @param value the object value
     * @return  the string representation
     */
     private String getStringRepresentation(final Object value) {
        final String strRepresentation = value.toString();
        if (!(value instanceof String)) {
            return strRepresentation;
        }

        // else it is a string
        return "\"" + strRepresentation + "\"";
    }
    
}
