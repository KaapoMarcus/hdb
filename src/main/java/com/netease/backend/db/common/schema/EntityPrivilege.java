package com.netease.backend.db.common.schema;

import java.io.Serializable;


public class EntityPrivilege implements Comparable<EntityPrivilege>, Cloneable,
        Serializable {

	private static final long	serialVersionUID	= -2458877496850151081L;
	
	private final String _schemaName;
    private final String _entityName;
    
    


    
    private boolean _read;
    private boolean _write;
    private boolean _execute;
    private boolean _grantOption;
    private boolean _allPrivileges;

    
	public EntityPrivilege(String schemaName, String entityName, boolean read,
			boolean write, boolean execute, boolean grant, boolean all) {
		this._schemaName = (schemaName == null ? "" : schemaName);
		this._entityName = (entityName == null ? "" : entityName);
		this._read = read;
		this._write = write;
		this._execute = execute;
		this._grantOption = grant;
		this._allPrivileges = all;
    }

    
	public EntityPrivilege(String qualifiedName, boolean read, boolean write,
			boolean execute, boolean grant, boolean all) {
		
		final int dotPosition = qualifiedName.indexOf(".");
		String schemaName = "";
		if (dotPosition != -1) {
			schemaName = qualifiedName.substring(0, dotPosition);
		}
		final String entityName = qualifiedName.substring(dotPosition + 1);

		this._schemaName = schemaName;
		this._entityName = entityName;
		this._read = read;
		this._write = write;
		this._execute = execute;
		this._grantOption = grant;
		this._allPrivileges = all;
	}





























    
    public EntityPrivilegeDiff getPrivilegeDiff(EntityPrivilege otherPrivilege) {
        if (!this.getSchemaName().equals(otherPrivilege.getSchemaName())
                || !this.getEntityName().equals(otherPrivilege.getEntityName())) { throw new IllegalArgumentException(
                "the qualified name of source entity-privilege:["
                        + this.getSchemaName() + "." + this.getEntityName()
                        + "] does not matching the other:["
                        + otherPrivilege.getSchemaName() + "."
                        + otherPrivilege.getEntityName() + "]"); }

        final EntityPrivilege grantPrivilege = new EntityPrivilege(this
                .getSchemaName(), this.getEntityName(), !this
                .hasReadPrivilege()
                && otherPrivilege.hasReadPrivilege(), !this.hasWritePrivilege()
                && otherPrivilege.hasWritePrivilege(), !this
                .hasExecutePrivilege()
                && otherPrivilege.hasExecutePrivilege(), !this.hasGrantOption()
                && otherPrivilege.hasGrantOption(), !this.hasAllPrivileges()
                && otherPrivilege.hasAllPrivileges());
        final EntityPrivilege revokePrivilege = new EntityPrivilege(this
                .getSchemaName(), this.getEntityName(), this
                .hasReadPrivilege()
                && !otherPrivilege.hasReadPrivilege(), this.hasWritePrivilege()
                && !otherPrivilege.hasWritePrivilege(), this
                .hasExecutePrivilege()
                && !otherPrivilege.hasExecutePrivilege(), this.hasGrantOption()
                && !otherPrivilege.hasGrantOption(), this.hasAllPrivileges()
                && !otherPrivilege.hasAllPrivileges());

        final EntityPrivilegeDiff privilegeDiff = new EntityPrivilegeDiff(
                grantPrivilege, revokePrivilege);
        return privilegeDiff;
    }

    
    public void reset() {
        this._read = false;
        this._write = false;
        this._execute = false;
        this._grantOption = false;
        this._allPrivileges = false;
    }

    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EntityPrivilege)) { return false; }
        if (this == obj) { return true; }
        final EntityPrivilege p = (EntityPrivilege) obj;

        return this.getQualifiedName().equals(p.getQualifiedName());
    }

    
    @Override
    public int hashCode() {
        return this.getQualifiedName().hashCode();
    }

    public int compareTo(EntityPrivilege o) {
        int n = this.getQualifiedName().compareTo(o.getQualifiedName());
        if (n != 0) { return n; }
        n = Boolean.valueOf(this._allPrivileges).compareTo(
                Boolean.valueOf(o._allPrivileges));
        if (n != 0) { return n; }
        n = Boolean.valueOf(this._write).compareTo(Boolean.valueOf(o._write));
        if (n != 0) { return n; }
        n = Boolean.valueOf(this._read).compareTo(Boolean.valueOf(o._read));
        if (n != 0) { return n; }
        n = Boolean.valueOf(this._execute).compareTo(
                Boolean.valueOf(o._execute));
        if (n != 0) { return n; }
        n = Boolean.valueOf(this._grantOption).compareTo(
                Boolean.valueOf(o._grantOption));
        if (n != 0) { return n; }
        return 0;
    }

    @Override
    public String toString() {
        String string = "";

        if (null != this.getSchemaName() && !"".equals(this.getSchemaName())) {
            string += this.getSchemaName() + ".";
        }
        string += this.getEntityName() + ": [";
        
        if (this.hasAllPrivileges()) {
            string += "ALL PRIVILEGE";
        } else if (this.hasNonePrivileges()){
        } else {
            if (this.hasReadPrivilege()) {
                string += "READ,";
            }
            if (this.hasWritePrivilege()) {
                string += "WRITE,";
            }
            if (this.hasExecutePrivilege()) {
                string += "EXEC,";
            }
            if (this.hasGrantOption()) {
                string += "GRANT-OPTION,";
            }
            if (",".equals(string.substring(string.length() - 1))) {
                string = string.substring(0, string.length() - 1);
            }
        }
        string = string + "]";

        return string;
    }

    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    public String getSchemaName() {
        return this._schemaName;
    }

    public String getEntityName() {
        return this._entityName;
    }

    
    public String getQualifiedName() {
        if (null == this.getSchemaName() || "".equals(this.getSchemaName())) {
            return this.getEntityName();
        } else {
            return this.getSchemaName() + "." + this.getEntityName();
        }
    }

    public boolean hasReadPrivilege() {
        return this._read;
    }

    public boolean hasWritePrivilege() {
        return this._write;
    }

    public boolean hasExecutePrivilege() {
        return this._execute;
    }

    public boolean hasGrantOption() {
        return this._grantOption;
    }

    public boolean hasAllPrivileges() {
        return this._allPrivileges;
    }

    public boolean hasNonePrivileges() {
        return !this.hasAllPrivileges() && !this.hasReadPrivilege()
                && !this.hasWritePrivilege() && !this.hasExecutePrivilege()
                && !this.hasGrantOption();
    }
    
    public boolean hasOnlyGrantOption() {
		return this.hasGrantOption() && !this.hasAllPrivileges()
				&& !this.hasReadPrivilege() && !this.hasWritePrivilege()
				&& !this.hasExecutePrivilege();
    }

    
    public boolean hasSamePrivilege(EntityPrivilege anotherEntityPrivilege) {
        if (!this.equals(anotherEntityPrivilege)) { return false; }
        if (this.hasAllPrivileges() != anotherEntityPrivilege
                .hasAllPrivileges()) { return false; }
        if (!this.hasAllPrivileges()) {
            if (this.hasReadPrivilege() != anotherEntityPrivilege
                    .hasReadPrivilege()
                    || this.hasWritePrivilege() != anotherEntityPrivilege
                            .hasWritePrivilege()
                    || this.hasExecutePrivilege() != anotherEntityPrivilege
                            .hasExecutePrivilege()
                    || this.hasGrantOption() != anotherEntityPrivilege
                            .hasGrantOption()) { return false; }
        }

        return true;
    }

	
	
	
	




}
