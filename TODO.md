## Disable Autobuild in workspace

public static boolean enableAutoBuild(boolean enable) throws
CoreException {
IWorkspace workspace= ResourcesPlugin.getWorkspace();
IWorkspaceDescription desc= workspace.getDescription();
boolean isAutoBuilding= desc.isAutoBuilding();
if (isAutoBuilding != enable) {
desc.setAutoBuilding(enable);
workspace.setDescription(desc);
}
return isAutoBuilding;
}