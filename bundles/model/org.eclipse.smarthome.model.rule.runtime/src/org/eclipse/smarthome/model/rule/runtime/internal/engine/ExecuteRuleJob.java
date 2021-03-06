/**
 * Copyright (c) 2014 openHAB UG (haftungsbeschraenkt) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.model.rule.runtime.internal.engine;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.smarthome.core.scriptengine.Script;
import org.eclipse.smarthome.core.scriptengine.ScriptEngine;
import org.eclipse.smarthome.core.scriptengine.ScriptExecutionException;
import org.eclipse.smarthome.model.core.ModelRepository;
import org.eclipse.smarthome.model.rule.rules.Rule;
import org.eclipse.smarthome.model.rule.rules.RuleModel;
import org.eclipse.smarthome.model.rule.runtime.internal.RuleModelRuntimeActivator;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Implementation of Quartz {@link Job}-Interface. It takes a rule
 * and simply executes it.
 * 
 * @author Kai Kreuzer - Initial contribution and API
 */
public class ExecuteRuleJob implements Job {

	private static final Logger logger = LoggerFactory.getLogger(ExecuteRuleJob.class);
		
	public static final String JOB_DATA_RULEMODEL = "model";
	public static final String JOB_DATA_RULENAME = "rule";
	
	@Inject
	private Injector injector;
	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		String modelName = (String) context.getJobDetail().getJobDataMap().get(JOB_DATA_RULEMODEL);				
		String ruleName = (String) context.getJobDetail().getJobDataMap().get(JOB_DATA_RULENAME);
		
		ModelRepository modelRepository = RuleModelRuntimeActivator.modelRepositoryTracker.getService();
		ScriptEngine scriptEngine = RuleModelRuntimeActivator.scriptEngineTracker.getService();
		
		if(modelRepository!=null && scriptEngine!=null) {
			EObject model = modelRepository.getModel(modelName);
			if (model instanceof RuleModel) {
				RuleModel ruleModel = (RuleModel) model;
				Rule rule = getRule(ruleModel, ruleName);
				if(rule!=null) {
					Script script = scriptEngine.newScriptFromXExpression(rule.getScript());
					logger.debug("Executing scheduled rule '{}'", rule.getName());
					try {
						script.execute(RuleContextHelper.getContext(rule,injector));
					} catch (ScriptExecutionException e) {
						logger.error("Error during the execution of rule {}", rule.getName(), e.getCause());
					}
				} else {
					logger.debug("Scheduled rule '{}' does not exist", ruleName);
				}
			} else {
				logger.debug("Rule file '{}' does not exist", modelName);
			}
		}
	}

	private Rule getRule(RuleModel ruleModel, String ruleName) {
		for(Rule rule : ruleModel.getRules()) {
			if(rule.getName().equals(ruleName)) {
				return rule;
			}
		}
		return null;
	}
}