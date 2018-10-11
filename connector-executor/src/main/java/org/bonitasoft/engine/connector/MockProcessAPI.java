package org.bonitasoft.engine.connector;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.engine.api.ProcessAPI;
import org.bonitasoft.engine.bpm.actor.ActorCriterion;
import org.bonitasoft.engine.bpm.actor.ActorInstance;
import org.bonitasoft.engine.bpm.actor.ActorMappingExportException;
import org.bonitasoft.engine.bpm.actor.ActorMappingImportException;
import org.bonitasoft.engine.bpm.actor.ActorMember;
import org.bonitasoft.engine.bpm.actor.ActorNotFoundException;
import org.bonitasoft.engine.bpm.actor.ActorUpdater;
import org.bonitasoft.engine.bpm.bar.BusinessArchive;
import org.bonitasoft.engine.bpm.category.Category;
import org.bonitasoft.engine.bpm.category.CategoryCriterion;
import org.bonitasoft.engine.bpm.category.CategoryNotFoundException;
import org.bonitasoft.engine.bpm.category.CategoryUpdater;
import org.bonitasoft.engine.bpm.comment.ArchivedComment;
import org.bonitasoft.engine.bpm.comment.Comment;
import org.bonitasoft.engine.bpm.connector.ArchivedConnectorInstance;
import org.bonitasoft.engine.bpm.connector.ConnectorCriterion;
import org.bonitasoft.engine.bpm.connector.ConnectorExecutionException;
import org.bonitasoft.engine.bpm.connector.ConnectorImplementationDescriptor;
import org.bonitasoft.engine.bpm.connector.ConnectorInstance;
import org.bonitasoft.engine.bpm.connector.ConnectorNotFoundException;
import org.bonitasoft.engine.bpm.contract.ContractDefinition;
import org.bonitasoft.engine.bpm.contract.ContractViolationException;
import org.bonitasoft.engine.bpm.data.ArchivedDataInstance;
import org.bonitasoft.engine.bpm.data.ArchivedDataNotFoundException;
import org.bonitasoft.engine.bpm.data.DataDefinition;
import org.bonitasoft.engine.bpm.data.DataInstance;
import org.bonitasoft.engine.bpm.data.DataNotFoundException;
import org.bonitasoft.engine.bpm.document.ArchivedDocument;
import org.bonitasoft.engine.bpm.document.ArchivedDocumentNotFoundException;
import org.bonitasoft.engine.bpm.document.Document;
import org.bonitasoft.engine.bpm.document.DocumentAttachmentException;
import org.bonitasoft.engine.bpm.document.DocumentCriterion;
import org.bonitasoft.engine.bpm.document.DocumentException;
import org.bonitasoft.engine.bpm.document.DocumentNotFoundException;
import org.bonitasoft.engine.bpm.document.DocumentValue;
import org.bonitasoft.engine.bpm.flownode.ActivityDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.flownode.ActivityExecutionException;
import org.bonitasoft.engine.bpm.flownode.ActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceCriterion;
import org.bonitasoft.engine.bpm.flownode.ActivityInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.ArchivedActivityInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedFlowNodeInstance;
import org.bonitasoft.engine.bpm.flownode.ArchivedFlowNodeInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.ArchivedHumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.EventCriterion;
import org.bonitasoft.engine.bpm.flownode.EventInstance;
import org.bonitasoft.engine.bpm.flownode.FlowNodeExecutionException;
import org.bonitasoft.engine.bpm.flownode.FlowNodeInstance;
import org.bonitasoft.engine.bpm.flownode.FlowNodeInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.FlowNodeType;
import org.bonitasoft.engine.bpm.flownode.HumanTaskInstance;
import org.bonitasoft.engine.bpm.flownode.SendEventException;
import org.bonitasoft.engine.bpm.flownode.TaskPriority;
import org.bonitasoft.engine.bpm.flownode.TimerEventTriggerInstance;
import org.bonitasoft.engine.bpm.flownode.TimerEventTriggerInstanceNotFoundException;
import org.bonitasoft.engine.bpm.flownode.UserTaskNotFoundException;
import org.bonitasoft.engine.bpm.parameter.ParameterCriterion;
import org.bonitasoft.engine.bpm.parameter.ParameterInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstance;
import org.bonitasoft.engine.bpm.process.ArchivedProcessInstanceNotFoundException;
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition;
import org.bonitasoft.engine.bpm.process.InvalidProcessDefinitionException;
import org.bonitasoft.engine.bpm.process.Problem;
import org.bonitasoft.engine.bpm.process.ProcessActivationException;
import org.bonitasoft.engine.bpm.process.ProcessDefinition;
import org.bonitasoft.engine.bpm.process.ProcessDefinitionNotFoundException;
import org.bonitasoft.engine.bpm.process.ProcessDeployException;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfo;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoCriterion;
import org.bonitasoft.engine.bpm.process.ProcessDeploymentInfoUpdater;
import org.bonitasoft.engine.bpm.process.ProcessEnablementException;
import org.bonitasoft.engine.bpm.process.ProcessExecutionException;
import org.bonitasoft.engine.bpm.process.ProcessExportException;
import org.bonitasoft.engine.bpm.process.ProcessInstance;
import org.bonitasoft.engine.bpm.process.ProcessInstanceCriterion;
import org.bonitasoft.engine.bpm.process.ProcessInstanceNotFoundException;
import org.bonitasoft.engine.bpm.process.V6FormDeployException;
import org.bonitasoft.engine.bpm.supervisor.ProcessSupervisor;
import org.bonitasoft.engine.exception.AlreadyExistsException;
import org.bonitasoft.engine.exception.BonitaException;
import org.bonitasoft.engine.exception.ContractDataNotFoundException;
import org.bonitasoft.engine.exception.CreationException;
import org.bonitasoft.engine.exception.DeletionException;
import org.bonitasoft.engine.exception.ExecutionException;
import org.bonitasoft.engine.exception.FormMappingNotFoundException;
import org.bonitasoft.engine.exception.NotFoundException;
import org.bonitasoft.engine.exception.RetrieveException;
import org.bonitasoft.engine.exception.SearchException;
import org.bonitasoft.engine.exception.UpdateException;
import org.bonitasoft.engine.expression.Expression;
import org.bonitasoft.engine.expression.ExpressionEvaluationException;
import org.bonitasoft.engine.form.FormMapping;
import org.bonitasoft.engine.identity.User;
import org.bonitasoft.engine.identity.UserNotFoundException;
import org.bonitasoft.engine.job.FailedJob;
import org.bonitasoft.engine.operation.Operation;
import org.bonitasoft.engine.search.SearchOptions;
import org.bonitasoft.engine.search.SearchResult;

public class MockProcessAPI implements ProcessAPI {

	@Override
	public ProcessDefinition deploy(BusinessArchive businessArchive)
			throws AlreadyExistsException, ProcessDeployException, V6FormDeployException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessDefinition deploy(DesignProcessDefinition designProcessDefinition)
			throws AlreadyExistsException, ProcessDeployException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enableProcess(long processDefinitionId)
			throws ProcessDefinitionNotFoundException, ProcessEnablementException {
		// TODO Auto-generated method stub

	}

	@Override
	public void disableProcess(long processDefinitionId)
			throws ProcessDefinitionNotFoundException, ProcessActivationException {
		// TODO Auto-generated method stub

	}

	@Override
	public ProcessDefinition getProcessDefinition(long processDefinitionId) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteProcessDefinition(long processDefinitionId) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteProcessDefinitions(List<Long> processDefinitionIds) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public ProcessDefinition deployAndEnableProcess(DesignProcessDefinition designProcessDefinition)
			throws ProcessDeployException, ProcessEnablementException, AlreadyExistsException,
			InvalidProcessDefinitionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessDefinition deployAndEnableProcess(BusinessArchive businessArchive)
			throws ProcessDeployException, ProcessEnablementException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Problem> getProcessResolutionProblems(long processDefinitionId)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfProcessDeploymentInfos() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProcessDeploymentInfo getProcessDeploymentInfo(long processDefinitionId)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProcessDeploymentInfo(long processDefinitionId,
			ProcessDeploymentInfoUpdater processDeploymentInfoUpdater)
			throws ProcessDefinitionNotFoundException, UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfos(int startIndex, int maxResults,
			ProcessDeploymentInfoCriterion sortCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfActors(long processDefinitionId) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActorInstance getActor(long actorId) throws ActorNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActorInstance> getActors(long processDefinitionId, int startIndex, int maxResults,
			ActorCriterion sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActorMember> getActorMembers(long actorId, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfActorMembers(long actorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfUsersOfActor(long actorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfRolesOfActor(long actorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfGroupsOfActor(long actorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfMembershipsOfActor(long actorId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActorInstance updateActor(long actorId, ActorUpdater actorUpdater)
			throws ActorNotFoundException, UpdateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addUserToActor(long actorId, long userId) throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addUserToActor(String actorName, ProcessDefinition processDefinition, long userId)
			throws ActorNotFoundException, CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addGroupToActor(long actorId, long groupId) throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addGroupToActor(String actorName, long groupId, ProcessDefinition processDefinition)
			throws ActorNotFoundException, CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addRoleToActor(long actorId, long roleId) throws CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addRoleToActor(String actorName, ProcessDefinition processDefinition, long roleId)
			throws ActorNotFoundException, CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addRoleAndGroupToActor(long actorId, long roleId, long groupId) throws CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActorMember addRoleAndGroupToActor(String actorName, ProcessDefinition processDefinition, long roleId,
			long groupId) throws ActorNotFoundException, CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeActorMember(long actorMemberId) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importActorMapping(long processDefinitionId, String xmlContent) throws ActorMappingImportException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importActorMapping(long processDefinitionId, byte[] actorMappingXML)
			throws ActorMappingImportException {
		// TODO Auto-generated method stub

	}

	@Override
	public String exportActorMapping(long processDefinitionId) throws ActorMappingExportException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category createCategory(String name, String description) throws AlreadyExistsException, CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfCategories() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Category> getCategories(int startIndex, int maxResults, CategoryCriterion sortCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Category getCategory(long categoryId) throws CategoryNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProcessDefinitionToCategory(long categoryId, long processDefinitionId)
			throws AlreadyExistsException, CreationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void addProcessDefinitionsToCategory(long categoryId, List<Long> processDefinitionIds)
			throws AlreadyExistsException, CreationException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getNumberOfCategories(long processDefinitionId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfProcessDefinitionsOfCategory(long categoryId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosOfCategory(long categoryId, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getCategoriesOfProcessDefinition(long processDefinitionId, int startIndex, int maxResults,
			CategoryCriterion sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateCategory(long categoryId, CategoryUpdater updater)
			throws CategoryNotFoundException, UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteCategory(long categoryId) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public long removeProcessDefinitionsFromCategory(long categoryId, int startIndex, int maxResults)
			throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long removeCategoriesFromProcessDefinition(long processDefinitionId, int startIndex, int maxResults)
			throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfUncategorizedProcessDefinitions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProcessDeploymentInfo> getUncategorizedProcessDeploymentInfos(int startIndex, int maxResults,
			ProcessDeploymentInfoCriterion sortCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataDefinition> getActivityDataDefinitions(long processDefinitionId, String activityName,
			int startIndex, int maxResults)
			throws ProcessDefinitionNotFoundException, ActivityDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfActivityDataDefinitions(long processDefinitionId, String activityName)
			throws ProcessDefinitionNotFoundException, ActivityDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<DataDefinition> getProcessDataDefinitions(long processDefinitionId, int startIndex, int maxResults)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfProcessDataDefinitions(long processDefinitionId) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, byte[]> getProcessResources(long processDefinitionId, String filenamesPattern)
			throws RetrieveException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getExternalProcessResource(long processDefinitionId, String fileName)
			throws RetrieveException, FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getLatestProcessDefinitionId(String processName) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getSupportedStates(FlowNodeType nodeType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getProcessDefinitionId(String name, String version) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProcessDeploymentInfo> getStartableProcessDeploymentInfosForActors(Set<Long> actorIds, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAllowedToStartProcess(long processDefinitionId, Set<Long> actorIds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ActorInstance getActorInitiator(long processDefinitionId)
			throws ActorNotFoundException, ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosStartedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosCanBeStartedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfos(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCategoriesToProcess(long processDefinitionId, List<Long> categoryIds)
			throws AlreadyExistsException, CreationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeCategoriesFromProcess(long processDefinitionId, List<Long> categoryIds) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchUncategorizedProcessDeploymentInfos(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchUncategorizedProcessDeploymentInfosSupervisedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchUncategorizedProcessDeploymentInfosCanBeStartedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, ProcessDeploymentInfo> getProcessDeploymentInfosFromIds(List<Long> processDefinitionIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConnectorImplementationDescriptor getConnectorImplementation(long processDefinitionId, String connectorName,
			String connectorVersion) throws ConnectorNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConnectorImplementationDescriptor> getConnectorImplementations(long processDefinitionId, int startIndex,
			int maxsResults, ConnectorCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfConnectorImplementations(long processDefinitionId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<Long, ActorInstance> getActorsFromActorIds(List<Long> actorIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForGroup(long groupId, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForGroups(List<Long> groupIds,
			int startIndex, int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForRole(long roleId, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForRoles(List<Long> roleIds,
			int startIndex, int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForUser(long userId, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosWithActorOnlyForUsers(List<Long> userIds,
			int startIndex, int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DesignProcessDefinition getDesignProcessDefinition(long processDefinitionId)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosSupervisedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosCanBeStartedByUsersManagedBy(
			long managerUserId, SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessSupervisor createProcessSupervisorForUser(long processDefinitionId, long userId)
			throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessSupervisor createProcessSupervisorForRole(long processDefinitionId, long roleId)
			throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessSupervisor createProcessSupervisorForGroup(long processDefinitionId, long groupId)
			throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessSupervisor createProcessSupervisorForMembership(long processDefinitionId, long groupId, long roleId)
			throws CreationException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserProcessSupervisor(long processDefinitionId, long userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteSupervisor(long supervisorId) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteSupervisor(Long processDefinitionId, Long userId, Long roleId, Long groupId)
			throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public SearchResult<ProcessSupervisor> searchProcessSupervisors(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getCategoriesUnrelatedToProcessDefinition(long processDefinitionId, int startIndex,
			int maxResults, CategoryCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfProcessDeploymentInfosUnrelatedToCategory(long categoryId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProcessDeploymentInfo> getProcessDeploymentInfosUnrelatedToCategory(long categoryId, int startIndex,
			int maxResults, ProcessDeploymentInfoCriterion sortingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<User> searchUsersWhoCanStartProcessDefinition(long processDefinitionId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, ProcessDeploymentInfo> getProcessDeploymentInfosFromProcessInstanceIds(
			List<Long> processInstanceIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, ProcessDeploymentInfo> getProcessDeploymentInfosFromArchivedProcessInstanceIds(
			List<Long> archivedProcessInstantsIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] exportBarProcessContentUnderHome(long processDefinitionId) throws ProcessExportException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disableAndDeleteProcessDefinition(long processDefinitionId)
			throws ProcessDefinitionNotFoundException, ProcessActivationException, DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<User> getPossibleUsersOfHumanTask(long processDefinitionId, String humanTaskName, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getUserIdsForActor(long processDefinitionId, String actorName, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void purgeClassLoader(long processDefinitionId) throws ProcessDefinitionNotFoundException, UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getNumberOfParameterInstances(long processDefinitionId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ParameterInstance getParameterInstance(long processDefinitionId, String parameterName)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ParameterInstance> getParameterInstances(long processDefinitionId, int startIndex, int maxResults,
			ParameterCriterion sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<FormMapping> searchFormMappings(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FormMapping getFormMapping(long formMappingId) throws FormMappingNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchOpenProcessInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchProcessInstances(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchFailedProcessInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchFailedProcessInstancesSupervisedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchOpenProcessInstancesSupervisedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfProcessDataInstances(long processInstanceId) throws ProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfActivityDataInstances(long activityInstanceId) throws ActivityInstanceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ProcessInstance> getProcessInstances(int startIndex, int maxResults,
			ProcessInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ArchivedProcessInstance> getArchivedProcessInstances(int startIndex, int maxResults,
			ProcessInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ArchivedActivityInstance> getArchivedActivityInstances(long sourceProcessInstanceId, int startIndex,
			int maxResults, ActivityInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ActivityInstance> getOpenActivityInstances(long processInstanceId, int startIndex, int maxResults,
			ActivityInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfOpenedActivityInstances(long processInstanceId) throws ProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfProcessInstances() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getNumberOfArchivedProcessInstances() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteProcessInstance(long processInstanceId) throws DeletionException {
		// TODO Auto-generated method stub

	}

	@Override
	public long deleteProcessInstances(long processDefinitionId, int startIndex, int maxResults)
			throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long deleteArchivedProcessInstances(long processDefinitionId, int startIndex, int maxResults)
			throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long deleteArchivedProcessInstancesInAllStates(List<Long> sourceProcessInstanceIds)
			throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long deleteArchivedProcessInstancesInAllStates(long sourceProcessInstanceId) throws DeletionException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProcessInstance startProcess(long processDefinitionId)
			throws ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcess(long processDefinitionId, Map<String, Serializable> initialVariables)
			throws ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcess(long processDefinitionId, List<Operation> operations,
			Map<String, Serializable> context)
			throws ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcess(long userId, long processDefinitionId) throws UserNotFoundException,
			ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcess(long userId, long processDefinitionId, List<Operation> operations,
			Map<String, Serializable> context) throws UserNotFoundException, ProcessDefinitionNotFoundException,
			ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcess(long userId, long processDefinitionId,
			Map<String, Serializable> initialVariables)
			throws ProcessDefinitionNotFoundException, ProcessActivationException, ProcessExecutionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcessWithInputs(long processDefinitionId,
			Map<String, Serializable> instantiationInputs) throws ProcessDefinitionNotFoundException,
			ProcessActivationException, ProcessExecutionException, ContractViolationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance startProcessWithInputs(long userId, long processDefinitionId,
			Map<String, Serializable> instantiationInputs) throws ProcessDefinitionNotFoundException,
			ProcessActivationException, ProcessExecutionException, ContractViolationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeFlowNode(long flownodeInstanceId) throws FlowNodeExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeFlowNode(long userId, long flownodeInstanceId) throws FlowNodeExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ActivityInstance> getActivities(long processInstanceId, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProcessInstance getProcessInstance(long processInstanceId) throws ProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ActivityInstance getActivityInstance(long activityInstanceId) throws ActivityInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FlowNodeInstance getFlowNodeInstance(long flowNodeInstanceId) throws FlowNodeInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedActivityInstance getArchivedActivityInstance(long sourceActivityInstanceId)
			throws ActivityInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HumanTaskInstance> getAssignedHumanTaskInstances(long userId, int startIndex, int maxResults,
			ActivityInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HumanTaskInstance> getPendingHumanTaskInstances(long userId, int startIndex, int maxResults,
			ActivityInstanceCriterion pagingCriterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfAssignedHumanTaskInstances(long userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<Long, Long> getNumberOfOpenTasks(List<Long> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfPendingHumanTaskInstances(long userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HumanTaskInstance getHumanTaskInstance(long activityInstanceId) throws ActivityInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventInstance> getEventInstances(long rootContainerId, int startIndex, int maxResults,
			EventCriterion sortingType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void assignUserTask(long userTaskId, long userId) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void assignUserTaskIfNotAssigned(long userTaskId, long userId) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateActorsOfUserTask(long userTaskId) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataInstance> getProcessDataInstances(long processInstanceId, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataInstance getProcessDataInstance(String dataName, long processInstanceId) throws DataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProcessDataInstance(String dataName, long processInstanceId, Serializable dataValue)
			throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProcessDataInstances(long processInstanceId, Map<String, Serializable> dataNameValues)
			throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataInstance> getActivityDataInstances(long activityInstanceId, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataInstance getActivityDataInstance(String dataName, long activityInstanceId) throws DataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateActivityDataInstance(String dataName, long activityInstanceId, Serializable dataValue)
			throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateActivityTransientDataInstance(String dataName, long activityInstanceId, Serializable dataValue)
			throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataInstance> getActivityTransientDataInstances(long activityInstanceId, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataInstance getActivityTransientDataInstance(String dataName, long activityInstanceId)
			throws DataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getActivityReachedStateDate(long activityInstanceId, String state) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateActivityInstanceVariables(long activityInstanceId, Map<String, Serializable> variables)
			throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateActivityInstanceVariables(List<Operation> operations, long activityInstanceId,
			Map<String, Serializable> expressionContexts) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateDueDateOfTask(long userTaskId, Date dueDate) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public long getOneAssignedUserTaskInstanceOfProcessInstance(long processInstanceId, long userId)
			throws ProcessInstanceNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getOneAssignedUserTaskInstanceOfProcessDefinition(long processDefinitionId, long userId)
			throws ProcessDefinitionNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getActivityInstanceState(long activityInstanceId) throws ActivityInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canExecuteTask(long activityInstanceId, long userId)
			throws ActivityInstanceNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void releaseUserTask(long userTaskId) throws ActivityInstanceNotFoundException, UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ArchivedProcessInstance> getArchivedProcessInstances(long processInstanceId, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedProcessInstance getFinalArchivedProcessInstance(long sourceProcessInstanceId)
			throws ArchivedProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivityStateById(long activityInstanceId, int stateId) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setActivityStateByName(long activityInstanceId, String state) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setProcessInstanceState(ProcessInstance processInstance, String state) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTaskPriority(long userTaskInstanceId, TaskPriority priority) throws UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Serializable> executeConnectorOnProcessDefinition(String connectorDefinitionId,
			String connectorDefinitionVersion, Map<String, Expression> connectorInputParameters,
			Map<String, Map<String, Serializable>> inputValues, long processDefinitionId)
			throws ConnectorExecutionException, ConnectorNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> executeConnectorOnProcessDefinition(String connectorDefinitionId,
			String connectorDefinitionVersion, Map<String, Expression> connectorInputParameters,
			Map<String, Map<String, Serializable>> inputValues, List<Operation> operations,
			Map<String, Serializable> operationInputValues, long processDefinitionId)
			throws ConnectorExecutionException, ConnectorNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedHumanTaskInstance> searchArchivedHumanTasks(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchAssignedTasksManagedBy(long managerUserId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchPendingTasksSupervisedBy(long userId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchPendingTasksForUser(long userId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchPendingTasksAssignedToUser(long userId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchPendingTasksManagedBy(long managerUserId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchAssignedAndPendingHumanTasksFor(long rootProcessDefinitionId,
			long userId, SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchAssignedAndPendingHumanTasks(long rootProcessDefinitionId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchAssignedAndPendingHumanTasks(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Long, Long> getNumberOfOverdueOpenTasks(List<Long> userIds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelProcessInstance(long processInstanceId) throws ProcessInstanceNotFoundException, UpdateException {
		// TODO Auto-generated method stub

	}

	@Override
	public void retryTask(long activityInstanceId)
			throws ActivityInstanceNotFoundException, ActivityExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeMessageCouple(long messageInstanceId, long waitingMessageEventId) throws ExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable evaluateExpressionOnProcessDefinition(Expression expression, Map<String, Serializable> context,
			long processDefinitionId) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long countComments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long countAttachments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void sendSignal(String signalName) throws SendEventException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(String messageName, Expression targetProcess, Expression targetFlowNode,
			Map<Expression, Expression> messageContent) throws SendEventException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessage(String messageName, Expression targetProcess, Expression targetFlowNode,
			Map<Expression, Expression> messageContent, Map<Expression, Expression> correlations)
			throws SendEventException {
		// TODO Auto-generated method stub

	}

	@Override
	public ArchivedProcessInstance getArchivedProcessInstance(long archivedProcessInstanceId)
			throws ArchivedProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedFlowNodeInstance getArchivedFlowNodeInstance(long archivedFlowNodeInstanceId)
			throws ArchivedFlowNodeInstanceNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedComment getArchivedComment(long archivedCommentId) throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ConnectorInstance> searchConnectorInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedConnectorInstance> searchArchivedConnectorInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<HumanTaskInstance> getHumanTaskInstances(long rootProcessInstanceId, String taskName, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HumanTaskInstance getLastStateHumanTaskInstance(long rootProcessInstanceId, String taskName)
			throws NotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedActivityInstance> searchArchivedActivities(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ActivityInstance> searchActivities(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<FlowNodeInstance> searchFlowNodeInstances(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedFlowNodeInstance> searchArchivedFlowNodeInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchMyAvailableHumanTasks(long userId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<Comment> searchComments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment addProcessComment(long processInstanceId, String comment) throws CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Comment addProcessCommentOnBehalfOfUser(long processInstanceId, String comment, long userId)
			throws CreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Comment> getComments(long processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<Comment> searchCommentsManagedBy(long managerUserId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<Comment> searchCommentsInvolvingUser(long userId, SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getChildrenInstanceIdsOfProcessInstance(long processInstanceId, int startIndex, int maxResults,
			ProcessInstanceCriterion criterion) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isInvolvedInProcessInstance(long userId, long processInstanceId)
			throws ProcessInstanceNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInvolvedInHumanTaskInstance(long userId, long humanTaskInstanceId)
			throws ActivityInstanceNotFoundException, UserNotFoundException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isManagerOfUserInvolvedInProcessInstance(long managerUserId, long processInstanceId)
			throws BonitaException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public long getProcessInstanceIdFromActivityInstanceId(long activityInstanceId)
			throws ProcessInstanceNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getProcessDefinitionIdFromProcessInstanceId(long processInstanceId)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getProcessDefinitionIdFromActivityInstanceId(long activityInstanceId)
			throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SearchResult<ArchivedComment> searchArchivedComments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedHumanTaskInstance> searchArchivedHumanTasksManagedBy(long managerUserId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchOpenProcessInstancesInvolvingUser(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessInstance> searchOpenProcessInstancesInvolvingUsersManagedBy(long managerUserId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedProcessInstance> searchArchivedProcessInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedProcessInstance> searchArchivedProcessInstancesInAllStates(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedProcessInstance> searchArchivedProcessInstancesSupervisedBy(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedProcessInstance> searchArchivedProcessInstancesInvolvingUser(long userId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchHumanTaskInstances(SearchOptions searchOptions)
			throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<HumanTaskInstance> searchAssignedTasksSupervisedBy(long supervisorId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedHumanTaskInstance> searchArchivedHumanTasksSupervisedBy(long supervisorId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionsAtProcessInstanciation(long processInstanceId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionOnCompletedProcessInstance(long processInstanceId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionsOnProcessInstance(long processInstanceId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionsOnProcessDefinition(long processDefinitionId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionsOnActivityInstance(long activityInstanceId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> evaluateExpressionsOnCompletedActivityInstance(long activityInstanceId,
			Map<Expression, Map<String, Serializable>> expressions) throws ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<FailedJob> getFailedJobs(int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replayFailedJob(long jobDescriptorId) throws ExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void replayFailedJob(long jobDescriptorId, Map<String, Serializable> parameters) throws ExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public ArchivedDataInstance getArchivedProcessDataInstance(String dataName, long sourceProcessInstanceId)
			throws ArchivedDataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedDataInstance getArchivedActivityDataInstance(String dataName, long sourceActivityInstanceId)
			throws ArchivedDataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ArchivedDataInstance> getArchivedProcessDataInstances(long sourceProcessInstanceId, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ArchivedDataInstance> getArchivedActivityDataInstances(long sourceActivityInstanceId, int startIndex,
			int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> getPossibleUsersOfPendingHumanTask(long humanTaskInstanceId, int startIndex, int maxResults) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<User> searchUsersWhoCanExecutePendingHumanTask(long humanTaskInstanceId,
			SearchOptions searchOptions) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosWithAssignedOrPendingHumanTasksFor(
			long userId, SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosWithAssignedOrPendingHumanTasksSupervisedBy(
			long supervisorId, SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ProcessDeploymentInfo> searchProcessDeploymentInfosWithAssignedOrPendingHumanTasks(
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Map<String, Long>> getFlownodeStateCounters(long processInstanceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<TimerEventTriggerInstance> searchTimerEventTriggerInstances(long processInstanceId,
			SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date updateExecutionDateOfTimerEventTriggerInstance(long timerEventTriggerInstanceId, Date executionDate)
			throws TimerEventTriggerInstanceNotFoundException, UpdateException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractDefinition getUserTaskContract(long userTaskId) throws UserTaskNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractDefinition getProcessContract(long processDefinitionId) throws ProcessDefinitionNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeUserTask(long userTaskInstanceId, Map<String, Serializable> inputs)
			throws UserTaskNotFoundException, ContractViolationException, FlowNodeExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeUserTask(long userId, long userTaskInstanceId, Map<String, Serializable> inputs)
			throws UserTaskNotFoundException, ContractViolationException, FlowNodeExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable getUserTaskContractVariableValue(long userTaskInstanceId, String name)
			throws ContractDataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getProcessInputValueDuringInitialization(long processInstanceId, String name)
			throws ContractDataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Serializable getProcessInputValueAfterInitialization(long processInstanceId, String name)
			throws ContractDataNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> getUserTaskExecutionContext(long userTaskInstanceId)
			throws UserTaskNotFoundException, ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> getArchivedUserTaskExecutionContext(long archivedUserTaskInstanceId)
			throws UserTaskNotFoundException, ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> getProcessInstanceExecutionContext(long processInstanceId)
			throws ProcessInstanceNotFoundException, ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> getArchivedProcessInstanceExecutionContext(long archivedProcessInstanceId)
			throws ProcessInstanceNotFoundException, ExpressionEvaluationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document attachDocument(long processInstanceId, String documentName, String fileName, String mimeType,
			String url) throws ProcessInstanceNotFoundException, DocumentAttachmentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document updateDocument(long documentId, DocumentValue documentValue)
			throws ProcessInstanceNotFoundException, DocumentAttachmentException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document addDocument(long processInstanceId, String documentName, String description,
			DocumentValue documentValue)
			throws ProcessInstanceNotFoundException, DocumentAttachmentException, AlreadyExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document attachDocument(long processInstanceId, String documentName, String fileName, String mimeType,
			byte[] documentContent) throws ProcessInstanceNotFoundException, DocumentAttachmentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document attachNewDocumentVersion(long processInstanceId, String documentName, String fileName,
			String mimeType, String url) throws DocumentAttachmentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document attachNewDocumentVersion(long processInstanceId, String documentName, String contentFileName,
			String contentMimeType, byte[] documentContent) throws DocumentAttachmentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getDocument(long documentId) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document removeDocument(long documentId) throws DocumentNotFoundException, DeletionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Document> getLastVersionOfDocuments(long processInstanceId, int pageIndex, int numberPerPage,
			DocumentCriterion pagingCriterion) throws ProcessInstanceNotFoundException, DocumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getDocumentContent(String storageId) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getLastDocument(long processInstanceId, String documentName) throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getDocumentAtProcessInstantiation(long processInstanceId, String documentName)
			throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document getDocumentAtActivityInstanceCompletion(long activityInstanceId, String documentName)
			throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getNumberOfDocuments(long processInstanceId) throws DocumentException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SearchResult<Document> searchDocuments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<Document> searchDocumentsSupervisedBy(long userId, SearchOptions searchOptions)
			throws UserNotFoundException, SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedDocument> searchArchivedDocuments(SearchOptions searchOptions) throws SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SearchResult<ArchivedDocument> searchArchivedDocumentsSupervisedBy(long userId, SearchOptions searchOptions)
			throws UserNotFoundException, SearchException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedDocument getArchivedProcessDocument(long archivedProcessDocumentId)
			throws ArchivedDocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchivedDocument getArchivedVersionOfProcessDocument(long sourceObjectId)
			throws ArchivedDocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Document> getDocumentList(long processInstanceId, String name, int fromIndex, int numberOfResult)
			throws DocumentNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDocumentList(long processInstanceId, String name, List<DocumentValue> documentsValues)
			throws DocumentNotFoundException, DocumentException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteContentOfArchivedDocument(long archivedDocumentId)
			throws DocumentException, DocumentNotFoundException {
		// TODO Auto-generated method stub

	}

}
