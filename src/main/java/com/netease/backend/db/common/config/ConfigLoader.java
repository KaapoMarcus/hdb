package com.netease.backend.db.common.config;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.netease.backend.db.common.utils.AppSingletonSupport;
import com.netease.backend.db.common.utils.InitHelper;


public class ConfigLoader<T extends PropertiesConfig> {

	
	private static final String				DEFAULT_CFG_FILE_ENC	= "iso8859-1";

	private final PropertiesConfigHelper<T>	cfgHelper;
	
	private final String					cfgDir;
	
	private final String					cfgExt;

	
	private final ReadWriteLock				lock					= new ReentrantReadWriteLock();
	private final Lock						readLock				= lock
																			.readLock();
	private final Lock						writeLock				= lock
																			.writeLock();

	
	private AppSingletonSupport				singletonSupport		= null;
	
	private final ConfigLoader<?>			parantLoader;

	
	public ConfigLoader(
		String cfgDir,
		String cfgExt,
		PropertiesConfigHelper<T> cfgHelper)
	{
		if (cfgHelper == null)
			throw new NullPointerException(
					" config properties helper should not be null!");
		this.cfgHelper = cfgHelper;
		if (cfgDir == null)
			throw new NullPointerException(this.getType()
					+ " config dir must not be null!");
		this.cfgDir = cfgDir;
		if (cfgExt == null)
			throw new NullPointerException(this.getType()
					+ " config file extention must not be null!");
		this.cfgExt = cfgExt;

		parantLoader = null;
	}

	
	public ConfigLoader(
		ConfigLoader<?> parantLoader,
		String cfgExt,
		PropertiesConfigHelper<T> cfgHelper)
	{
		if (parantLoader == null)
			throw new NullPointerException("");
		this.parantLoader = parantLoader;
		this.cfgDir = parantLoader.getCfgDir();

		if (cfgHelper == null)
			throw new NullPointerException(
					" config properties helper should not be null!");
		this.cfgHelper = cfgHelper;
		if (cfgExt == null)
			throw new NullPointerException(this.getType()
					+ " config file extention must not be null!");
		this.cfgExt = cfgExt;
	}

	
	public ConfigLoader<?> getParantLoader() {
		return parantLoader;
	}

	
	public String getCfgDir() {
		return cfgDir;
	}

	
	public String getCfgExt() {
		return cfgExt;
	}

	
	public Class<T> getTypeClass() {
		return this.getHelper().getTypeClass();
	}

	
	public String getType() {
		return this.getTypeClass().getSimpleName();
	}

	
	public PropertiesConfigHelper<T> getHelper() {
		return cfgHelper;
	}

	
	protected AppSingletonSupport getSingletonSupport() {
		checkState(this);
		return singletonSupport;
	}

	public void dispose() {
		final AppSingletonSupport singletonSupport = this.singletonSupport;
		if (singletonSupport != null) {
			try {
				singletonSupport.deactivate();
			} catch (final Exception ex) {
			}
		}
	}

	
	private final InitHelper	initHelper	= new InitHelper();

	
	protected static void checkState(
		ConfigLoader<?> target)
	{
		if (!target.initHelper.isInitd())
			throw new IllegalStateException(target.getType()
					+ " configuration loader not initd!");
	}

	
	public void init()
		throws ConfigException
	{
		final InitHelper helper = this.initHelper;
		
		if (!helper.tryInit()) 
			return;

		this.CreateCfgDirIfNonExist();
		this.lockCfgDir();

		helper.initd(); 
	}

	
	private void CreateCfgDirIfNonExist()
		throws ConfigException
	{
		final String cfgDir = this.cfgDir;
		final File file = new File(cfgDir);
		if (!file.exists()) {
			file.mkdir();
		} else {
			if (file.isFile())
				throw new ConfigException(this.getType()
						+ "�����ļ�Ŀ¼����ʧ�ܣ��Ѵ���ͬ���ļ�����" + cfgDir);
		}
	}

	private void lockCfgDir()
		throws ConfigException
	{
		
		final ConfigLoader<?> parantLoader = this.getParantLoader();
		if (parantLoader != null) {
			parantLoader.init();
			this.singletonSupport = parantLoader.getSingletonSupport();
		} else {
			this.singletonSupport = new AppSingletonSupport(cfgDir);
		}
		try {
			
			if (!singletonSupport.activated())
				throw new ConfigException(this.getType()
						+ "���ù�������ʼ��ʧ�ܣ������ļ�Ŀ¼�����ѱ�ռ�ã�");
		} catch (final IOException ex) {
			throw new ConfigException(this.getType() + "���ù�������ʼ��I/O����"
					+ ex.getMessage());
		}
	}

	
	public T loadConfig(
		String name, String cfgFileName)
		throws ConfigException
	{
		Properties props = this.loadConfigProps(cfgFileName);
		if (props == null) {
			
			props = new Properties();
		}
		return this.getHelper().fromProperties(name, props, "" );
	}

	
	protected Properties loadConfigProps(
		String cfgFileName)
		throws ConfigException
	{
		if (cfgFileName == null)
			throw new NullPointerException(this.getTypeClass()
					+ "�����ļ����Ʋ���Ϊnull��");

		
		this.checkAppLock();

		
		
		
		
		
		
		
		
		
		
		
		final Lock readLock = this.readLock;
		readLock.lock();
		FileChannel fc = null;
		try {
			Properties props = null;

			
			final File file;
			if (((file = this.getFile(cfgFileName)) != null) && file.exists()) {
				fc = new RandomAccessFile(file, "r").getChannel();

				
				final ByteBuffer bytes = ByteBuffer.allocate(4096);
				fc.read(bytes);
				bytes.flip();

				final byte[] b = new byte[bytes.remaining()];
				bytes.get(b);
				props = new Properties();
				props.load(new ByteArrayInputStream(b));
			}

			return props;
		} catch (final IOException ex) {
			ex.printStackTrace();
			throw new ConfigException("��ȡ�����ļ�I/O����" + ex.getMessage());
		} finally {
			readLock.unlock();
			if (fc != null) {
				
				
				try {
					fc.close();
				} catch (final IOException ex) {
				}
			}
		}
	}

	
	public void saveConfig(
		T config, String cfgFileName)
		throws ConfigException
	{
		if ((cfgFileName == null) || (config == null))
			throw new NullPointerException(this.getType() + "�����ļ����ƻ����ݲ���Ϊnull��");
		this.saveConfigProps(config.toProperties(), config
						.getDescription(), cfgFileName);
	}

	
	protected void saveConfigProps(
		Properties cfgProps, String descirption, String cfgFileName)
		throws ConfigException
	{
		if ((cfgFileName == null) || (cfgProps == null))
			throw new NullPointerException(this.getType() + "�����ļ����ƻ����ݲ���Ϊnull��");

		
		this.checkAppLock();

		
		
		
		
		
		
		
		
		
		
		
		final Lock writeLock = this.writeLock;
		writeLock.lock();
		FileChannel fc = null;
		try {
			
			final File cfgFile = this.getOrCreateFile(cfgFileName);

			
			clearFileContent(cfgFile);

			fc = new RandomAccessFile(cfgFile, "rw").getChannel();
			final byte[] b = PropertiesConfigHelperBase.toPropertiesString(
					cfgProps, descirption).getBytes(DEFAULT_CFG_FILE_ENC);
			final ByteBuffer bytes = ByteBuffer.allocate(b.length);
			bytes.put(b);
			bytes.flip();
			fc.write(bytes);
			fc.force(false);
		} catch (final IOException ex) {
			throw new ConfigException("������д�������ļ�I/O����" + ex.getMessage());
		} finally {
			writeLock.unlock();
			
			try {
				if (fc != null)
					fc.close();
			} catch (final IOException ex) {
			}
		}
	}

	private static void clearFileContent(
		File cfgFile)
		throws FileNotFoundException, IOException
	{
		final FileOutputStream out = new FileOutputStream(cfgFile);
		out.flush();
		try {
			out.close();
		} catch (final Exception ex) {
		}
	}

	
	private void checkAppLock()
		throws ConfigException
	{
		checkState(this);
		if (!this.getSingletonSupport().isActive())
			throw new ConfigException(this.getType() + "�����ļ�Ŀ¼δ������ "
					+ this.getCfgDir());
	}

	
	private File getOrCreateFile(
		String fileName)
		throws IOException
	{
		final File file = this.getFile(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}

		return file;
	}

	
	private File getFile(
		String fileName)
	{
		final File file = new File(this.getCfgDir() + File.separator + fileName
				+ this.getCfgExt());
		return file;
	}

}
