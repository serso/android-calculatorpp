/*
 * Copyright 2013 serso aka se.solovyev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Contact details
 *
 * Email: se.solovyev@gmail.com
 * Site:  http://se.solovyev.org
 */

package org.solovyev.android.calculator;

import javax.annotation.Nonnull;

/**
 * User: Solovyev_S
 * Date: 20.09.12
 * Time: 16:40
 */
public enum CalculatorEventType {

	/*
	**********************************************************************
	*
	*                           CALCULATION
	*                           org.solovyev.android.calculator.CalculatorEvaluationEventData
	*
	**********************************************************************
	*/


	// @Nonnull CalculatorEditorViewState
	manual_calculation_requested,

	// @Nonnull org.solovyev.android.calculator.CalculatorOutput
	calculation_result,

	calculation_cancelled,

	// @Nonnull org.solovyev.android.calculator.CalculatorFailure
	calculation_failed,

	/*
	**********************************************************************
	*
	*                           CONVERSION
	*                           CalculatorConversionEventData
	*
	**********************************************************************
	*/
	conversion_started,

	// @Nonnull String conversion result
	conversion_result,

	// @Nonnull ConversionFailure
	conversion_failed,

	conversion_finished,

	/*
	**********************************************************************
	*
	*                           EDITOR
	*
	**********************************************************************
	*/

	// @Nonnull org.solovyev.android.calculator.CalculatorEditorChangeEventData
	editor_state_changed,
	editor_state_changed_light,

	// @Nonnull CalculatorDisplayChangeEventData
	display_state_changed,

	/*
	**********************************************************************
	*
	*                           ENGINE
	*
	**********************************************************************
	*/

	engine_preferences_changed,

	/*
	**********************************************************************
	*
	*                           HISTORY
	*
	**********************************************************************
	*/

	// @Nonnull CalculatorHistoryState
	history_state_added,

	// @Nonnull CalculatorHistoryState
	use_history_state,

	clear_history_requested,

	/*
	**********************************************************************
	*
	*                           MATH ENTITIES
	*
	**********************************************************************
	*/

	// @Nonnull IConstant
	use_constant,

	// @Nonnull Function
	use_function,

	// @Nonnull Operator
	use_operator,

	// @Nonnull IConstant
	constant_added,

	// @Nonnull Change<IConstant>
	constant_changed,

	// @Nonnull IConstant
	constant_removed,


	// @Nonnull Function
	function_removed,

	// @Nonnull Function
	function_added,

	// @Nonnull Change<IFunction>
	function_changed,

	/*
	**********************************************************************
	*
	*                           OTHER
	*
	**********************************************************************
	*/

	// List<Message>
	calculation_messages,

	show_history,
	show_history_detached,

	show_functions,
	show_functions_detached,

	show_vars,
	show_vars_detached,

	open_app,

	show_operators,
	show_operators_detached,

	show_settings,
	show_settings_detached,

	show_like_dialog,

	show_create_var_dialog,
	show_create_matrix_dialog,
	show_create_function_dialog,

	/**
	 * {@link DialogData}
	 */
	show_message_dialog,

	show_wiki_description,

	plot_graph,

	/**
	 * {@link org.solovyev.android.calculator.plot.PlotData}
	 */
	plot_data_changed,

	//String
	show_evaluation_error;

	public boolean isOfType(@Nonnull CalculatorEventType... types) {
		for (CalculatorEventType type : types) {
			if (this == type) {
				return true;
			}
		}

		return false;
	}

}
