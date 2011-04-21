package ru.tehkode.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.backends.FileBackend;
import ru.tehkode.permissions.config.Configuration;

/**
 *
 * @author code
 */
public class PermissionManager {

    protected Logger logger = Logger.getLogger("Minecraft");
    protected Map<String, PermissionUser> users = new HashMap<String, PermissionUser>();
    protected Map<String, PermissionGroup> groups = new HashMap<String, PermissionGroup>();
    protected PermissionBackend backend = null;
    protected PermissionGroup defaultGroup = null;
    protected Configuration config;

    public PermissionManager(Configuration config) {
        this.config = config;

        this.initBackend();
    }

    public void reset() {
        this.users.clear();
        this.groups.clear();
        this.defaultGroup = null;

        this.backend.reload();
    }

    public PermissionUser getUser(String username) {
        if (username == null || username.isEmpty()) {
            return null;
        }

        PermissionUser user = users.get(username);

        if (user == null) {
            user = this.backend.getUser(username);
            if (user != null) {
                this.users.put(username, user);
            }
        }

        return user;
    }

    public PermissionGroup getGroup(String groupname) {
        if (groupname == null || groupname.isEmpty()) {
            return null;
        }

        PermissionGroup group = groups.get(groupname);

        if (group == null) {
            group = this.backend.getGroup(groupname);
            if (group != null) {
                this.groups.put(groupname, group);
            }
        }

        return group;
    }

    public boolean has(Player player, String permission){
        PermissionUser user = this.getUser(player.getName());

        if(user == null){
            return false;
        }

        return user.has(permission, player.getWorld().getName());
    }

    public void resetUser(String userName){
        this.users.put(userName, null);
    }

    public void resetGroup(String groupName){
        this.groups.put(groupName, null);
    }

    public boolean removeGroup(String groupName) {
        return backend.removeGroup(groupName);
    }

    public PermissionUser[] getUsers(String groupName) {
        return backend.getUsers(groupName);
    }

    public PermissionUser[] getUsers() {
        return backend.getUsers();
    }

    public PermissionGroup[] getGroups(String groupName) {
        return backend.getGroups(groupName);
    }

    public PermissionGroup[] getGroups() {
        return backend.getGroups();
    }

    public PermissionGroup getDefaultGroup() {
        if (this.defaultGroup == null) {
            this.defaultGroup = this.backend.getDefaultGroup();
        }

        return this.defaultGroup;
    }

    private void initBackend() {
        String backEnd = this.config.getString("permissions.backend");

        if (backEnd == null || backEnd.isEmpty()) {
            backEnd = PermissionBackend.defaultBackend; //Default backend
            this.config.setProperty("permissions.backend", backEnd);
            this.config.save();
        }
        
        this.backend = PermissionBackend.getBackend(backEnd, this, config);
    }

    public void setBackend(String backEnd){
        this.reset();
        this.backend = PermissionBackend.getBackend(backEnd, this, config);
    }

    public String getBackend() {
        return PermissionBackend.getBackendAlias(this.backend.getClass());
    }
}