import React from "react";
import { ResponsivePie } from "@nivo/pie";

/**
 * @description searches a collection `slices` for the index of an element with the
 * specified `id`.
 * 
 * @param { array } slices - array of objects to search for the specified `id`.
 * 
 * @param { integer } id - identifier of the slice to search for in the `slices` array.
 * 
 * @returns { integer } the index of the matching `id` within the input `slices` array.
 */
const _find = (slices, id) => {
  return slices.findIndex((entry) => entry.id === id);
};

/**
 * @description takes a value and a total parameter as input, returns the contribution
 * percentage based on value divided by total multiplied by 100.
 * 
 * @param { number } value - percentage to be calculated as a decimal value.
 * 
 * @param { number } total - total amount of values in the calculation, and it is
 * used to calculate the percentage change in value.
 * 
 * @returns { number } a decimal representation of the percentage contribution of the
 * given value to the total.
 */
const _contribution = (value, total) => {
  return ((value / total) * 100).toFixed(1);
};


/**
 * @description coalesces an array of slices by pushing a new slice onto the end if
 * there is not existing label equal to `label`, otherwise, it adds the value to the
 * corresponding slice's accumulator.
 * 
 * @param { array } slices - 1D or 2D arrangement of a dataset or feature matrix that
 * is being processed, and it contains an array of subarrays, each subarray representing
 * one slice of the data or feature space.
 * 
 * @param { number } value - value that is being added to or subtracted from the
 * corresponding slice within the `_find` function.
 * 
 * @param { string } label - label for the new slice that is being added or modified
 * in the `push` or `=` statements.
 */
const _coalesce = (slices, value, label) => {
  const idx = _find(slices, "others");
  if (idx === -1) {
    slices.push({
      id: "others",
      label: label,
      value: value,
      contrib: 0,
    });
  } else {
    slices[idx].value += value;
  }
};

/**
 * @description processes a dataset by grouping items into segments based on their
 * values and contributions to a total. It takes in data, key field, value field,
 * unique threshold, and others label and returns processed slices of the data.
 * 
 * @param { array } data - 2D array to be processed, containing the required information
 * for calculating and grouping the slices of the dataset into buckets.
 * 
 * @param { string } keyField - field that serves as the identifier for each slice
 * in the data set.
 * 
 * @param { number } valueField - field containing the numerical value of each item
 * in the data, which is used to determine whether a slice should be included or
 * excluded from the processed slices array based on the `unique` and `threshold` parameters.
 * 
 * @param { integer } unique - 4th condition for the aggregation of values, filtering
 * out slices that are not unique or have less than a specified threshold value.
 * 
 * @param { integer } threshold - value above which items are considered unique and
 * included in the resulting slice array, with values less than or equal to 3 excluded
 * and values greater than 4 but less than or equal to the threshold included.
 * 
 * @param { string } othersLabel - label for items that do not meet the unique or
 * threshold values and is used to provide a descriptive name for these items in the
 * resulting processed slices.
 * 
 * @returns { object } an array of objects containing `id`, `label`, `value`, and
 * `contrib` properties.
 */
const _slices = (
  data,
  keyField,
  valueField,
  unique,
  threshold,
  othersLabel = "Others",
) => {
  /**
   * @description pushes items to an array `slices` based on a condition involving
   * `unique` and `threshold`.
   * 
   * @param { object } slices - array that will be populated with the results of applying
   * the given conditions to the input data.
   * 
   * @param { object } item - item that is being evaluated for inclusion in the slice
   * set.
   * 
   * @returns { object } an array of objects representing the items to be included in
   * the chart, based on their values and uniqueness.
   */
  /**
   * @description updates the `contrib` property of the provided slice object by
   * calculating the sum of the values of the data array, multiplied by the `valueField`,
   * and adding it to the original value of the slice.
   * 
   * @param { _contribution. } slice - current slice of data being processed, which is
   * combined with the accumulated contribution from previous slices to calculate the
   * final contribution.
   * 
   * 		- `value`: The original value contained within `slice`.
   * 		- `contrib`: A computed property that calculates the contribution of each element
   * in `data` to the total value of `slice`. This is calculated by summing the value
   * of each element with its corresponding index, and then multiplying the result by
   * 10.
   * 
   * @returns { object } an object with an additional `contrib` property, which represents
   * the contribution made by the given slice of code to the overall total.
   */
  const processedSlices =
    data
      ?.reduce((slices, item) => {
        const value = parseInt(item[valueField]);
        if (unique <= 3 || (unique > 4 && value > threshold)) {
          slices.push({
            id: item[keyField],
            label: item[keyField],
            value: value,
            contrib: 0,
          });
        } else {
          _coalesce(slices, value, othersLabel);
        }
        return slices;
      }, [])
      ?.map((slice) => ({
        ...slice,
        contrib: _contribution(
          slice.value,
          data.reduce((total, i) => total + parseInt(i[valueField], 10), 0),
        ),
      })) || [];

  return processedSlices;
};

/**
 * @description generates a pie chart based on data, computing the total quantity and
 * labeling each slice with its corresponding percentage contribution to the overall
 * amount. It also displays an overlay with the total amount and label.
 * 
 * @param { array } data - 2D array of objects that contains the values for the pie
 * chart, with each object containing a value and a field label.
 * 
 * @param { string } field - name of a specific field from the given data object to
 * which the values in the data array are mapped to calculate the number of unique values.
 * 
 * @param { number } quantity - quantity of slices to display in the pie chart, and
 * is used in conjunction with the `unique` variable to determine the number of data
 * points to display in each slice.
 * 
 * @param { string } label - text to display adjacent to the pie chart, typically
 * providing additional information about the data being displayed.
 * 
 * @returns { object } a pie chart representing the distribution of a given dataset,
 * with options for customizing colors, margins, and tooltips.
 */
const Pie = ({
  data,
  amount = data?.length,
  field,
  quantity,
  label,
  threshold = 50,
}) => {
  const unique = new Set(data.map((d) => d[field])).size;
  const datum =
    unique === 0
      ? [{ value: 1, contrib: 100 }]
      : _slices(data, field, quantity, unique, threshold);

  return (
    <div className="pie">
      {/**
       * @description generates a pie chart with a given dataset, displaying the contribution
       * of each datum as a percentage of the total.
       * 
       * @param { number } data - 3D dataset to be visualized as a pie chart, which determines
       * the values and labels of the slice.
       * 
       * @param { array } colors - 2 different colors to be used for the arc and label
       * components, with the first color being used for the arcs when the number of unique
       * contributors is greater than 0, and the second color being used otherwise.
       * 
       * @param { number } margin - 80-pixel margins applied to the top, right, bottom, and
       * left sides of the pie chart.
       * 
       * @param { number } innerRadius - 0-based offset of the arc's center from its starting
       * angle, with a range of values between 0 and 1, and sets the radius of the inner
       * circle that determines the size of the slice's inner section when generating labels.
       * 
       * @param { number } padAngle - amount of additional space to leave between the border
       * of the pie slice and the arc label, enabling proper spacing and readability of the
       * labels.
       * 
       * @param { integer } activeOuterRadiusOffset - 80% buffer distance around the inner
       * circle of the pie chart, where the active section will be displayed with a darker
       * border color when the number of data points is greater than zero.
       * 
       * @param { integer } borderWidth - width of the border that surrounds the Pie chart,
       * which can be useful for adding visual cues or creating contrast in the graphical
       * representation of data.
       * 
       * @param { object } borderColor - color of the border that surrounds the pie chart,
       * with the option to adjust its intensity using the `modifiers` array.
       * 
       * @param { boolean } enableArcLinkLabels - visibility of arc link labels for each
       * slice, which will be displayed when the number of data points in that slice is
       * greater than zero.
       * 
       * @param { boolean } enableArcLabels - visibility of arc labels on the pie chart,
       * with a value of `true` showing the labels and `false` hiding them.
       * 
       * @param { string } arcLinkLabelsTextColor - color of the text for the arc link
       * labels when there are more than one, and it is set to `#eceded`.
       * 
       * @param { numeric value, expressed in pixels (or possibly some other measurement
       * unit). } arcLinkLabelsThickness - thickness of the labels for the arc links, which
       * can be set to a value between 1 and 5 inclusive to control the appearance of the
       * labels.
       * 
       * 		- from: "color" - Indicates that the thickness of the arc link labels is derived
       * from a color value.
       * 		- modifiers: [["darker", 0.2]] - Provides a darkening effect to the thickness
       * based on the provided factor (in this case, 0.2).
       * 
       * @param { object } arcLinkLabelsColor - color of the labels on the outer arc of the
       * pie chart, which can be set to a hex code or a gradient.
       * 
       * @param { Component } tooltip - element displayed when a user hovers over a slice
       * of the donut chart, displaying the percentage contribution of that slice to the
       * total value.
       * 
       * @param { object } theme - overall style and design of the visualization, including
       * the text font family and size.
       */}
      {/**
       * @description generates a pie chart with a responsive inner radius, border width,
       * and colors based on the given data. It also provides tooltips for each slice and
       * enables link labels when there are more than one slice.
       * 
       * @param { object } data - 0-based indexed array of data points for generating the
       * pie chart, which are used to calculate the inner and outer radii, angle offset,
       * and other properties of the chart.
       * 
       * @param { array } colors - 2 colors used to generate the arc labels and link, with
       * the first color (`unique > 0 ? ["#26de81"] : ["#5a646a"]`) being used for the inner
       * arc and the second color (`"#eceded"`)` being used for the arc links and labels.
       * 
       * @param { number } margin - 80-pixel buffer around the outer edges of the circle,
       * used to ensure clearance and avoid overlapping with neighboring elements or labels.
       * 
       * @param { number } innerRadius - innermost radius of each slice within the donut
       * chart, which affects the visual representation of each slice's area.
       * 
       * @param { number } padAngle - angle of the arc label's border around the outer
       * circumference, which can help to prevent overlapping labels and provide a more
       * consistent appearance for the arc labels.
       * 
       * @param { integer } activeOuterRadiusOffset - 8-pixel offset added to the outer
       * radius of the pie chart when the link is active, helping to improve the visualization
       * of the arc labels.
       * 
       * @param { number } borderWidth - width of the border surrounding the pie chart,
       * which can be used to adjust the visual appearance of the chart.
       * 
       * @param { `color` or more specifically a/an  `rgbaColor`. } borderColor - color of
       * the border surrounding the pie chart, with modifications to its darkness level
       * through the use of modifiers.
       * 
       * 		- `from`: The source of the color value. In this case, it is `color`, which means
       * that the color will be drawn from a palette of colors defined in the `colors` object.
       * 		- `modifiers`: An array of objects containing modifier functions that can be
       * applied to the color value. In this case, there is one modifier function called
       * `darker`, with a value of 0.2. This means that the color will be darkened by 20%
       * of its original value.
       * 
       * 	Overall, the `borderColor` property defines the color and modifiers for the border
       * of the responsive pie chart.
       * 
       * @param { boolean } enableArcLinkLabels - presence or absence of link labels on the
       * arcs of the pie chart, and when set to `true`, it enables link labels for each arc
       * segment based on its corresponding contrib percentage value.
       * 
       * @param { boolean } enableArcLabels - state of an arc label feature on the ResponsivePie
       * component, with a value of `false` indicating that labels are disabled and a value
       * of `true` indicating that labels are enabled.
       * 
       * @param { string } arcLinkLabelsTextColor - color of the labels displayed on the
       * arc when a link is clicked, with the specified value of `#eceded` indicating a
       * light gray color.
       * 
       * @param { number } arcLinkLabelsThickness - 3D line width of the labels attached
       * to the arcs in the donut chart, and determines how thick the lines will be displayed.
       * 
       * @param { color. } arcLinkLabelsColor - color of the labels displayed along the
       * circumference of the pie chart, which can be defined using a color value or a
       * modifier to adjust the color based on the dataset's darkness level.
       * 
       * 		- `from`: The property 'color' specifies the source for determining the color value.
       * 		- `modifiers`: An array containing a single object with a 'darker' property set
       * to 0.2, which adjusts the brightness of the color.
       * 
       * 	The `arcLinkLabelsColor` property has an Object value that is generated dynamically
       * based on these properties.
       * 
       * @param { Component } tooltip - tooltip content to be displayed when the user hovers
       * over the pie chart, which is defined as a JavaScript function that returns a React
       * component containing the desired tooltip text and layout.
       * 
       * @param { object } theme - overall visual style of the chart, including typography
       * and colors, through its nested `text` object that defines font family and font size.
       */}
      <ResponsivePie
        data={datum}
        colors={unique > 0 ? ["#26de81"] : ["#5a646a"]}
        margin={{ top: 80, right: 80, bottom: 80, left: 80 }}
        innerRadius={0.97}
        padAngle={2}
        activeOuterRadiusOffset={8}
        borderWidth={1}
        borderColor={{
          from: "color",
          modifiers: [["darker", 0.2]],
        }}
        enableArcLinkLabels={unique > 0}
        enableArcLabels={false}
        // arcLinkLabelsSkipAngle={10}
        arcLinkLabelsTextColor="#eceded"
        arcLinkLabelsThickness={2}
        arcLinkLabelsColor={{ from: "color" }}
        // arcLabelsSkipAngle={10}
        tooltip={(point) => {
          return (
            <div className="pie-tooltip">
              <div className="value">{point?.datum?.data?.contrib} %</div>
            </div>
          );
        }}
        theme={{
          text: {
            fontFamily: "Inter",
            fontSize: "12px",
          },
        }}
      />
      <div className="pie-overlay">
        <div className="amount">{amount?.toLocaleString()}</div>
        <div className="label">{label}</div>
      </div>
    </div>
  );
};

export default Pie;
