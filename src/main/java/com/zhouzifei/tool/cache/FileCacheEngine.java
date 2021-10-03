package com.zhouzifei.tool.cache;

import com.zhouzifei.tool.cache.common.SerializationUtils;
import com.zhouzifei.tool.cache.util.FileManager;
import com.zhouzifei.tool.entity.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件存储换成
 * @author 周子斐 (17600004572@163.com)
 *
 */
public class FileCacheEngine implements CacheEngine {

	private  static final FileManager FILE_MANAGER;
	static
	{
		FILE_MANAGER = new FileManager();
		FILE_MANAGER.init();
	}
	@Override
	public void add(String cacheName, Serializable key, Object value) {
		if(cacheName!=null&&key!=null&&value!=null){		
			try {
				byte[] data = SerializationUtils.serialize(value);
				FILE_MANAGER.add(cacheName, key, data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void add( Serializable key, Object value) {
		add(FOLDER_NAME, key, value);
	}
	@Override
	public Object get(String folderName, Serializable key) {
		if(folderName!=null&&key!=null)
		{
			try {
				byte[] data = FILE_MANAGER.get(folderName, key);
				return SerializationUtils.deserialize(data);
			} catch (IOException e) {			
				e.printStackTrace();
			}
		}
		return null;
	}
	@Override
	public Object get( Serializable key) {		
		return get(FOLDER_NAME, key);
	}

	@Override
	public void remove(String folderName, Serializable key) {
		if(folderName!=null&&key!=null)
		{
			FILE_MANAGER.remove(folderName, key);
		}
		
	}
	@Override
	public void remove(Serializable key) {
		remove(FOLDER_NAME, key);
		
	}
	@Override
	public void clear(String folderName) {
		if(folderName!=null)
		{
			try {
				FILE_MANAGER.clear(folderName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	@Override
	public void clear() {
		clear(FOLDER_NAME);
		
	}
	@Override
	public void stop() {
		try {
			FILE_MANAGER.close();
		} catch (IOException e) {		
			e.printStackTrace();
		}
		
	}
	public File[] getList(){
		return getList(FOLDER_NAME);
	}
	public File[] getAll(){
		return getList();
	}
	public File[] getList(String cacheName){
		return FILE_MANAGER.getList(cacheName);
	}
	public static void main(String[] args) {

		CacheEngine engine = new FileCacheEngine();//创建缓存
		engine.add("name",  "tngou");//存在  默认的储存库为 default
		engine.add("table", "time", new Date()); //缓存库 table ,key=tile,value= 时间对象
		final VirtualFile virtualFile = new VirtualFile();
		virtualFile.setSuffix("2342");
		virtualFile.setFullFilePath("12312");
		virtualFile.setFileHash("43434");
		engine.add("aliyun", "virtualFile", virtualFile); //缓存库 table ,key=tile,value= 时间对象
		CacheEngine engine1 = new FileCacheEngine();
		Object name = engine1.get("name"); //取值
		Object time = engine1.get("table", "time"); //取值
		Object vi= engine1.get("aliyun", "virtualFile"); //取值
		System.out.println(vi);
		System.err.println(name+":"+time);
		String userHome = System.getProperty("user.home");//用户的主目录System.out.println("userHome:   "+userHome);
		System.out.println(userHome);
		//engine.clear("table");//清除缓存
		//engine.stop(); //关闭

	}
}
