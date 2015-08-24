package com.zzn.aenote.http.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.zzn.aenote.http.BaseService;
import com.zzn.aenote.http.utils.StringUtil;
import com.zzn.aenote.http.utils.UtilUniqueKey;
import com.zzn.aenote.http.vo.ProjectVO;

/**
 * 群组相关信息操作接口类
 * 
 * @author Shawn
 */
public class ProjectService extends BaseService {
	private static final Logger logger = Logger.getLogger(ProjectService.class);
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 创建新项目
	 * 
	 * @param project_name
	 * @param head
	 * @param parent_id
	 * @param root_id
	 * @param create_user
	 * @param address
	 * @param longitude
	 * @param latitude
	 * @return
	 */
	public ProjectVO createProject(String project_name, String head,
			String parent_id, String root_id, String create_user,
			String address, String longitude, String latitude, String root_name) {
		try {
			ProjectVO project = new ProjectVO();
			Map<String, Object> data = new HashMap<String, Object>();
			String project_id = UtilUniqueKey.getKey(create_user);
			data.put("project_id", project_id);
			project.setPROJECT_ID(project_id);
			if (StringUtil.isEmpty(parent_id)) {
				parent_id = project_id;
			}
			data.put("parent_id", parent_id);
			project.setPARENT_ID(parent_id);
			if (StringUtil.isEmpty(root_id)) {
				root_id = project_id;
			}
			data.put("root_id", root_id);
			project.setROOT_ID(root_id);
			project.setROOT_PROJECT_NAME(root_name);
			String projectNameHead = "";
			if (!parent_id.equals(project_id)) {
				List<Map<String, Object>> parentProject = queryProjectByID(parent_id);
				if (parentProject != null && parentProject.size() > 0) {
					projectNameHead = parentProject.get(0).get("project_name")
							.toString()
							+ "-";
				}
			}
			data.put("project_name", project_name);
			project.setPROJECT_NAME(projectNameHead + project_name);
			data.put("head", head);
			project.setHEAD(head);
			data.put("create_user", create_user);
			project.setCREATE_USER(create_user);
			data.put("address", address);
			project.setADDRESS(address);
			data.put("longitude", longitude);
			project.setLONGITUDE(longitude);
			data.put("latitude", latitude);
			project.setLATITUDE(latitude);
			String date = format.format(new Date(System.currentTimeMillis()));
			project.setCREATE_TIME(date);
			data.put("create_time", date);
			getJdbc().execute(getSql("create_project", data));
			return project;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据项目id查询
	 * 
	 * @param project_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryProjectByID(String project_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			projectList = getJdbc().queryForList(
					getSql("query_project_by_id", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectList;
	}

	/**
	 * 查询创建过的项目
	 * 
	 * @param user_id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryProjectByCreateUser(String user_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("create_user", user_id);
			projectList = getJdbc().queryForList(
					getSql("query_project_by_create_user", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectList;
	}

	/**
	 * 查询当前加入的项目
	 * 
	 * @param user_id
	 * @return
	 */
	public List<Map<String, Object>> queryCurrentProject(String user_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("user_id", user_id);
			projectList = getJdbc().queryForList(
					getSql("query_user_current_project", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectList;
	}

	/**
	 * 查询子项目个数
	 * 
	 * @param project_id
	 * @return
	 */
	public int queryLeafCount(String project_id) {
		int count = 0;
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			count = getJdbc().queryForInt(getSql("query_leaf_count", data)) - 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (count < 0) {
			count = 0;
		}
		return count;
	}

	public boolean updateParentProject(String project_id, String parent_id,
			String root_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			data.put("parent_id", parent_id);
			data.put("root_id", root_id);
			getJdbc().execute(getSql("update_parent_project", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateRootProject(String old_root_id, String new_root_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("old_root_id", old_root_id);
			data.put("new_root_id", new_root_id);
			getJdbc().execute(getSql("update_root_project", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteProject(String project_id) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			getJdbc().execute(getSql("delete_project", data));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 查询组织架构
	 * 
	 * @param project_id
	 * @return
	 */
	public List<Map<String, Object>> queryProjectStructure(String project_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rootList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> parentList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			rootList = getJdbc().queryForList(
					getSql("query_root_project", data));
			parentList = getJdbc().queryForList(
					getSql("query_parent_project", data));
			projectList.addAll(rootList);
			if (rootList != null && rootList.size() > 0) {
				if (!rootList.get(0).get("root_id").toString()
						.equals(project_id)) {
					if (parentList != null && parentList.size() > 0) {
						Map<String, Object> parent = parentList.get(0);
						if (!parent
								.get("project_id")
								.toString()
								.equals(rootList.get(0).get("root_id")
										.toString())) {
							parent.put("parent_id",
									rootList.get(0).get("root_id").toString());
							projectList.add(parent);
						}
						if (!parent.get("project_id").toString()
								.equals(project_id)) {
							projectList
									.addAll(queryParentLeafProject(parent
											.get("project_id").toString()));
						}
					}
				}
			}
			projectList.addAll(queryLeafProjects(project_id));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return projectList;
	}

	public List<Map<String, Object>> queryParentLeafProject(String project_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			projectList = getJdbc().queryForList(
					getSql("query_leaf_project", data));
			if (projectList != null && projectList.size() > 0) {
				for (Map<String, Object> project : projectList) {
					if (!project.get("project_id").toString()
							.equals(project_id)) {
						result.add(project);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public List<Map<String, Object>> queryLeafProjects(String project_id) {
		List<Map<String, Object>> projectList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			projectList = getJdbc().queryForList(
					getSql("query_leaf_project", data));
			if (projectList != null && projectList.size() > 0) {
				for (Map<String, Object> project : projectList) {
					if (!project.get("project_id").toString()
							.equals(project_id)) {
						result.add(project);
						result.addAll(queryLeafProjects(project.get(
								"project_id").toString()));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询项目成员
	 * 
	 * @param project_id
	 * @return
	 */
	public List<Map<String, Object>> queryProjectUsers(String project_id) {
		List<Map<String, Object>> userList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			userList = getJdbc().queryForList(
					getSql("query_project_users", data));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userList;
	}

	public boolean updateProjectName(String project_id, String project_name) {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("project_id", project_id);
			data.put("project_name", project_name);
			getJdbc().execute(getSql("update_project_name", data));
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
