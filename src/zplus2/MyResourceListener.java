package zplus2;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;

import zplus2.service.BuildJavaCode;

public class MyResourceListener implements IResourceChangeListener {
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		//Log.trace("接收到监听事件");
		switch (event.getType()) {
		case IResourceChangeEvent.POST_BUILD:
			//Log.trace("接收到监听事件IResourceChangeEvent.POST_BUILD");
			break;
		case IResourceChangeEvent.POST_CHANGE:
			//Log.trace("接收到监听事件IResourceChangeEvent.POST_CHANGE");
			myVisit(event);
			break;
		case IResourceChangeEvent.PRE_BUILD:
			//Log.trace("接收到监听事件IResourceChangeEvent.PRE_BUILD");
			break;
		case IResourceChangeEvent.PRE_CLOSE:
			//Log.trace("接收到监听事件IResourceChangeEvent.PRE_CLOSE");
			break;
		}
	}

	private void myVisit(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new IResourceDeltaVisitor(){
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					IResource resource = delta.getResource();
					if (resource.getType() == IResource.FILE) {
						String changeFilePathName = resource.getFullPath().toString();///zplus/src/zplus/MyResourceListener.java
						String workspacePath = Platform.getInstanceLocation().getURL().getPath();
						if(isInterestFile(changeFilePathName)){
							changeFilePathName = workspacePath.substring(0, workspacePath.length()-1) +  changeFilePathName;
							Log.info("file["+changeFilePathName+"] content is changed");
							BuildJavaCode.build(new File(changeFilePathName));
							return false;
						}
					}
					return true;
				}
				
				private boolean isInterestFile(String filePath){
					//Log.debug(filePath);
					if(!filePath.endsWith("CodeMap.xml")){
						return false;
					}
					if(filePath.contains("/bin/") || filePath.contains("/classes/")){
						return false;
					}
					return true;
				}
			});
		} catch (CoreException e) {
			Log.trace(e.getMessage());
		}
		
	}
	
/*
	public void visit(IResourceDelta delta) {
		IResource resource = delta.getResource();
		System.out.println("resource.getType():" + resource.getType());
		if ((delta.getFlags() & IResourceDelta.CONTENT) == 0) {
			System.out.println("内容变化了");
		}

		visitMyCodeFile(resource);
		// only interested in files with the "txt" extension
		if (resource.getType() == IResource.FILE) {
			// visitMyCodeFile(resource);
			// changed.add(resource);
			// files.add(resource);
		}
		if (resource.getType() == IResource.FOLDER) {
			// changed.add(resource);
			// folders.add(resource);
		}
		if (resource.getType() == IResource.PROJECT) {
			// changed.add(resource);
			// projects.add(resource);
		}
	}*/

/*	private void visitMyCodeFile(IResource resource) {
		System.out.println("resource.getLocationURI().toString():" + resource.getLocationURI().toString());
		Log.trace("resource.getLocationURI().toString():" + resource.getLocationURI().toString());
	}*/
}
