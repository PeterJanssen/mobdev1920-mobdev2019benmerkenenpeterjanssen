/*
 * Copyright (C) 2018 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package be.pxl.mobdev2019.cityWatch.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import be.pxl.mobdev2019.cityWatch.R

import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.flag.FlagView


@SuppressLint("ViewConstructor")
class CustomFlag(context: Context, layout: Int) : FlagView(context, layout) {

  private val textView: TextView = findViewById(R.id.flag_color_code)
  private val view: View = findViewById(R.id.flag_color_layout)

  @SuppressLint("SetTextI18n")
  override fun onRefresh(colorEnvelope: ColorEnvelope) {
    textView.text = "#" + colorEnvelope.hexCode
    view.setBackgroundColor(colorEnvelope.color)
  }
}
