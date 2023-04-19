package com.aem.core.models;

import com.aem.common.annotations.ParseStringUtils;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReusableFields {

	@ParseStringUtils
	private String htmlID;

	@ParseStringUtils
	private String desktopPT;

	@ParseStringUtils
	private String desktopPR;

	@ParseStringUtils
	private String desktopPB;

	@ParseStringUtils
	private String desktopPL;

	@ParseStringUtils
	private String mobilePT;

	@ParseStringUtils
	private String mobilePR;

	@ParseStringUtils
	private String mobilePB;

	@ParseStringUtils
	private String mobilePL;

}
