package com.warptronic.itdm.analyzer;

import java.util.ArrayList;

import com.warptronic.itdm.config.CredentialsException;
import com.warptronic.itdm.config.ProgramOptions;
import com.warptronic.itdm.utils.StringUtils;

public class Case {

	private final String user;
	private static final String USER = "-user";
	
	private final String pwd;
	private static final String PWD = "-pwd";
	
	private final String cookie;
	private static final String COOKIE = "-cookie";
	
	private final String authtype;
	private static final String AUTHTYPE = "-authtype";
	
	private final String projectname;
	private static final String PROJECTNAME = "-projectname";
	
	private final ProgramOptions programOptions;
	
	public Case(String user, String pwd, String cookie, String authtype, String projectname) throws CredentialsException {
		
		this.user = user;
		this.pwd = pwd;
		this.cookie = cookie;
		this.authtype = authtype;
		this.projectname = projectname;
		
		this.programOptions = ProgramOptions.fromArgs(generateArgs());
	}
	
	private String[] generateArgs() throws CredentialsException {
		
		ArrayList<String>args = new ArrayList<String>();
		
		if (!StringUtils.isNullOrEmpty(user)) {
			args.add(USER);
			args.add(user);
		}
		
		if (!StringUtils.isNullOrEmpty(pwd)) {
			args.add(PWD);
			args.add(pwd);
		}
		
		if (!StringUtils.isNullOrEmpty(cookie)) {
			args.add(COOKIE);
			args.add(cookie);
		}
		
		if (!StringUtils.isNullOrEmpty(authtype)) {
			args.add(AUTHTYPE);
			args.add(authtype);
		}
		
		if (StringUtils.isNullOrEmpty(projectname)) {
			throw new CredentialsException("Project name cannot be null or empty");
		} else {
			args.add(PROJECTNAME);
			args.add(projectname);
		}
		
		return args.toArray(new String[0]);
	}
	
}





















