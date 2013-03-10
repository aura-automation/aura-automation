/**	   Copyright 


**/
package org.aa.auraconfig.resources.metadata;

import java.util.ArrayList;

/**
 *  *   SystemAttributes._ATTRIBUTE_METAINFO_NAME
    * SystemAttributes._ATTRIBUTE_METAINFO_TYPE
    * SystemAttributes._ATTRIBUTE_METAINFO_IS_COLLECTION
    * SystemAttributes._ATTRIBUTE_METAINFO_IS_REQUIRED
    * SystemAttributes._ATTRIBUTE_METAINFO_DEFAULT_VALUE
    * SystemAttributes._ATTRIBUTE_METAINFO_ENUM_INFO
    * SystemAttributes._ATTRIBUTE_METAINFO_IS_OBJECT
    * SystemAttributes._ATTRIBUTE_METAINFO_IS_REFERENCE 
 * @author Jatin
 *
 */
public class ResourceAttributeMetaData {

		private String name;
		
		private String type;
		
		private boolean collection;
		
		private boolean required;
		
		private String defaultValue;
		
		private ArrayList enumInfo;
		
		private String enumType;
		
		private boolean deprecated;
		
		private boolean removed;
		
		private String subType;
		
		private boolean object;
		
		private boolean reference;

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}

		/**
		 * @param type the type to set
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * @return the collection
		 */
		public boolean isCollection() {
			return collection;
		}

		/**
		 * @param collection the collection to set
		 */
		public void setCollection(boolean collection) {
			this.collection = collection;
		}

		/**
		 * @return the required
		 */
		public boolean isRequired() {
			return required;
		}

		/**
		 * @param required the required to set
		 */
		public void setRequired(boolean required) {
			this.required = required;
		}

		/**
		 * @return the defaultValue
		 */
		public String getDefaultValue() {
			return defaultValue;
		}

		/**
		 * @param defaultValue the defaultValue to set
		 */
		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		/**
		 * @return the object
		 */
		public boolean isObject() {
			return object;
		}

		/**
		 * @param object the object to set
		 */
		public void setObject(boolean object) {
			this.object = object;
		}

		/**
		 * @return the reference
		 */
		public boolean isReference() {
			return reference;
		}

		/**
		 * @param reference the reference to set
		 */
		public void setReference(boolean reference) {
			this.reference = reference;
		}

		/**
		 * @return the enumType
		 */
		public String getEnumType() {
			return enumType;
		}

		/**
		 * @param enumType the enumType to set
		 */
		public void setEnumType(String enumType) {
			this.enumType = enumType;
		}

		/**
		 * @return the deprecated
		 */
		public boolean isDeprecated() {
			return deprecated;
		}

		/**
		 * @param deprecated the deprecated to set
		 */
		public void setDeprecated(boolean deprecated) {
			this.deprecated = deprecated;
		}

		/**
		 * @return the removed
		 */
		public boolean isRemoved() {
			return removed;
		}

		/**
		 * @param removed the removed to set
		 */
		public void setRemoved(boolean removed) {
			this.removed = removed;
		}

		/**
		 * @return the subType
		 */
		public String getSubType() {
			return subType;
		}

		/**
		 * @param subType the subType to set
		 */
		public void setSubType(String subType) {
			this.subType = subType;
		}

		/**
		 * @return the enumInfo
		 */
		public ArrayList getEnumInfo() {
			return enumInfo;
		}

		/**
		 * @param enumInfo the enumInfo to set
		 */
		public void setEnumInfo(ArrayList enumInfo) {
			this.enumInfo = enumInfo;
		}
		
		
}
