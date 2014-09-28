/*
 *                    PDB web development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the individual
 * authors.  These should be listed in @author doc comments.
 *
 *
 * Created on Jul 8, 2009
 * Created by ap3
 *
 */

package org.biojava.bio.structure.align.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.biojava.bio.structure.align.ce.AbstractUserArgumentProcessor;
import org.biojava.bio.structure.align.ce.StartupParameters;
import org.biojava3.core.util.PrettyXMLWriter;
import org.biojava3.core.util.XMLWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** A container to persist config to the file system
 * 
 * @author Andreas Prlic
 *
 */
public class UserConfiguration
{

	private static final Logger logger = LoggerFactory.getLogger(UserConfiguration.class);
	
	public static final String PDB_FORMAT   = "PDB";
	public static final String MMCIF_FORMAT = "mmCif";

	public static final String TMP_DIR = "java.io.tmpdir";
	public static final String PDB_DIR = "PDB_DIR";
	
	public static final String lineSplit = System.getProperty("file.separator");

	
	
	private String pdbFilePath;
	private String cacheFilePath;
	private boolean isSplit;

	private boolean autoFetch;

	private String fileFormat;



	/**
	 * Default UserConfiguration:
	 * <ul>
	 * <li>split directory</li>
	 * <li>autofetch files</li>
	 * <li>default download location. This is the first specified of:
	 * 	<ol><li>PDB_DIR system property (for instance, -DPDB_DIR=/tmp)</li>
	 *   <li>PDB_DIR environment variable</li>
	 *   <li>System temp directory (java.io.tmpdir property)</li>
	 *   </ol>
	 *   if the provided path is not a directory or is not writable then 
	 *   the system's temp directory is used.
	 * </li>
	 * </ul>
	 */
	public UserConfiguration(){
		isSplit = true;
		autoFetch = true;
		       
		pdbFilePath = initPdbFilePath();
		// note that in initCacheFilePath, we set to the provided one (if readable) or to the same as pdbFilePath
		cacheFilePath = initCacheFilePath();
		
		fileFormat = PDB_FORMAT;
	}
	
	private String initPdbFilePath() {
		
		String path = null;
		
		String propertyName = PDB_DIR;
		
		String userProvidedDir = System.getProperty(propertyName);

		if ( userProvidedDir != null ) {

			path = userProvidedDir;
			logger.debug("Read dir from system property {}: {}", propertyName, path);
			File f = new File(path);
			if (!f.isDirectory()) {
				logger.warn(
						"Provided path {} (with system property {}) is not a directory. Using system's temp directory instead {}", 
						path, propertyName, System.getProperty(TMP_DIR));
				path = System.getProperty(TMP_DIR);
			} else if (!f.canWrite()) {
				logger.warn(
						"Provided path {} (with system property {}) is not a directory. Using system's temp directory instead {}", 
						path, propertyName, System.getProperty(TMP_DIR));
				path = System.getProperty(TMP_DIR);
			}
			

		} else {
			Map<String,String> env = System.getenv();

			if( env.containsKey(propertyName)) {
				path = env.get(propertyName);
				logger.debug("Read dir from environment variable {}: {}", propertyName, path);
				
				File f = new File(path);
				if (!f.isDirectory()) {
					logger.warn(
							"Provided path {} (with environment variable {}) is not a directory. Using system's temp directory instead {}", 
							path, propertyName, System.getProperty(TMP_DIR));
					path = System.getProperty(TMP_DIR);
				} else if (!f.canWrite()) {
					logger.warn(
							"Provided path {} (with environment variable {}) is not a directory. Using system's temp directory instead {}", 
							path, propertyName, System.getProperty(TMP_DIR));
					path = System.getProperty(TMP_DIR);
				}

			} else {
				path = System.getProperty(TMP_DIR);
				logger.warn("Could not read dir from system property {} or environment variable {}, "
						+ "using system's temp directory {}",
						propertyName, propertyName, path);
			}   
		}
		
		if ( ! path.endsWith(lineSplit) )
			path = path + lineSplit;
		
		return path;

	}

	private String initCacheFilePath() {
		
		String path = null;
		
		String propertyName = AbstractUserArgumentProcessor.CACHE_DIR;
		
		String userProvidedDir = System.getProperty(propertyName);

		if ( userProvidedDir != null ) {

			path = userProvidedDir;
			logger.debug("Read dir from system property {}: {}", propertyName, path);
			File f = new File(path);
			if (!f.isDirectory()) {
				logger.warn(
						"Provided path {} (with system property {}) is not a directory. Using system's temp directory instead {}", 
						path, propertyName, System.getProperty(TMP_DIR));
				path = System.getProperty(TMP_DIR);
			} else if (!f.canWrite()) {
				logger.warn(
						"Provided path {} (with system property {}) is not a directory. Using system's temp directory instead {}", 
						path, propertyName, System.getProperty(TMP_DIR));
				path = System.getProperty(TMP_DIR);
			}
			

		} else {
			Map<String,String> env = System.getenv();

			if( env.containsKey(propertyName)) {
				path = env.get(propertyName);
				logger.debug("Read dir from environment variable {}: {}", propertyName, path);
				
				File f = new File(path);
				if (!f.isDirectory()) {
					logger.warn(
							"Provided path {} (with environment variable {}) is not a directory. Using system's temp directory instead {}", 
							path, propertyName, System.getProperty(TMP_DIR));
					path = System.getProperty(TMP_DIR);
				} else if (!f.canWrite()) {
					logger.warn(
							"Provided path {} (with environment variable {}) is not a directory. Using system's temp directory instead {}", 
							path, propertyName, System.getProperty(TMP_DIR));
					path = System.getProperty(TMP_DIR);
				}

			} else {
				// NOTE in case of not provided, then it is set to same as pdbFilePath
				path = pdbFilePath;
				logger.info("Could not read cache dir from system property {} or environment variable {}, "
						+ "using PDB directory instead {}",
						propertyName, propertyName, path);
			}   
		}
		
		if ( ! path.endsWith(lineSplit) )
			path = path + lineSplit;
		
		return path;

	}
	
	public String getPdbFilePath()
	{
		return pdbFilePath;
	}

	public void setPdbFilePath(String pdbFilePath)
	{
		this.pdbFilePath = pdbFilePath;
	}

	public String getCacheFilePath() {
		return cacheFilePath;
	}

	public void setCacheFilePath(String cacheFilePath) {
		this.cacheFilePath = cacheFilePath;
	}

	public boolean isSplit() {
		return isSplit;
	}

	public void setSplit(boolean isSplit) {
		this.isSplit = isSplit;
	}

	public boolean getAutoFetch() {
		return autoFetch;
	}

	public void setAutoFetch(boolean autoFetch) {
		this.autoFetch = autoFetch;
	}

	/** convert Configuration to an XML file so it can be serialized
	 * 
	 * @param pw
	 * @return XMLWriter
	 * @throws IOException
	 */
	public XMLWriter toXML(PrintWriter pw) 
			throws IOException
			{

		XMLWriter     xw = new PrettyXMLWriter( pw);

		toXML(xw);
		return xw ;
			}


	/** convert Configuration to an XML file so it can be serialized
	 * add to an already existing xml file.
	 * 
	 * @param xw the XML writer to use
	 * @return the writer again
	 * @throws IOException
	 */

	public XMLWriter toXML(XMLWriter xw) 
			throws IOException
			{
		xw.printRaw("<?xml version='1.0' standalone='no' ?>");
		//xw.printRaw("<!DOCTYPE " + XML_CONTENT_TYPE + " SYSTEM '" + XML_DTD + "' >");
		xw.openTag("JFatCatConfig");

		xw.openTag("PDBFILEPATH");
		// we don;t serialize the tempdir...
		String tempdir = System.getProperty(TMP_DIR);
		if (! pdbFilePath.equals(tempdir))
			xw.attribute("path", pdbFilePath);

		xw.attribute("split", isSplit +"" );
		xw.attribute("autofetch", autoFetch+"");
		xw.attribute("fileFormat", fileFormat);
		xw.closeTag("PDBFILEPATH");

		xw.closeTag("JFatCatConfig");
		return xw ;

			}

	public static UserConfiguration fromStartupParams(StartupParameters params) {
		UserConfiguration config = new UserConfiguration();
		config.setPdbFilePath(params.getPdbFilePath());
		config.setAutoFetch(params.isAutoFetch());
		config.setSplit(params.isPdbDirSplit());
		// TODO support MMCif Files
		config.setFileFormat(UserConfiguration.PDB_FORMAT);
		return config;
	}

	public void setFileFormat (String fileFormat){
		this.fileFormat = fileFormat;
	}

	public String getFileFormat()
	{
		return fileFormat;
	}






}
