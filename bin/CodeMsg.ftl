package ${packageName};
/**
 * 保存错有错误信息
 * @author zhaojz
 *
 */
public class ${className} {
	<#list list as item>
		<#if item.msg??>
	
	/**
	 * ${item.msg}
	 */
	public static final String ${item.id}="${item.value}";
		<#else>
		
	/**
	 * ${item.label}
	 */
	public static class ${item.id}{
			<#list item.list as item2><#--第二级-->
				<#if item2.msg??>
		/**
		 * ${item2.msg}
		 */
		public static final String ${item2.id}="${item2.value}";	
				<#else>
				
		/**
		 * ${item2.label}
		 */
		public static class ${item2.id}{
					<#list item2.list as item3><#--第三级-->
						<#if item3.msg??>
			/**
			 * ${item3.msg}
			 */
			public static final String ${item3.id}="${item3.value}";	
						<#else>
						
			/**
			 * ${item3.label}
			 */
			public static class ${item3.id}{
							<#list item3.list as item4><#--第四级-->
								<#if item4.msg??>
				/**
				 * ${item4.msg}
				 */
				public static final String ${item4.id}="${item4.value}";					
								</#if>
							</#list>
			}
						</#if>
					</#list>
		}
				</#if>
			</#list>
	}
		</#if>
	</#list>
}