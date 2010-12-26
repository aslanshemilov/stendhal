/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.progress;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * Progress status window. For displaying quest information.
 */
public class ProgressLog {
	/** Width of the window content */ 
	private static final int PAGE_WIDTH = 400;
	/** Height of the window content */
	private static final int PAGE_HEIGHT = 300;
	/** Width of the index area of a page */
	private static final int INDEX_WIDTH = 150;
	
	/** The enclosing window */
	JDialog window;
	/** Category tabs */
	private final JTabbedPane tabs;
	
	/**
	 * Create a new ProgressLog.
	 * 
	 * @param name name of the window
	 */
	ProgressLog(String name) {
		window = new JDialog(j2DClient.get().getMainFrame(), name);
		
		tabs = new JTabbedPane();
		tabs.setFocusable(false);
		tabs.setPreferredSize(new Dimension(PAGE_WIDTH, PAGE_HEIGHT));
		tabs.addChangeListener(new TabChangeListener());
		
		window.add(tabs);
		window.pack();
	}
	
	/**
	 * Set the available categories.
	 * 
	 * @param pages category list
	 * @param query query for retrieving the index lists for the pages. Page
	 *	name will be used as the query parameter 
	 */
	void setPages(List<String> pages, ProgressStatusQuery query) {
		tabs.removeAll();
		for (String page : pages) {
			Page content = new Page();
			content.setIndexQuery(query, page);
			tabs.add(page, content);
		}
	}
	
	/**
	 * Set the subject index for a given page.
	 * 
	 * @param page category
	 * @param subjects index of available subjects
	 * @param onClick query for retrieving the data for a given subject. Subject
	 * 	name will be used as the query parameter
	 */
	void setPageIndex(String page, List<String> subjects, ProgressStatusQuery onClick) {
		int index = tabs.indexOfTab(page);
		if (index != -1) {
			Component comp = tabs.getComponent(index);
			if (comp instanceof Page) {
				((Page) comp).setIndex(subjects, onClick);
			}
		}
	}
	
	/**
	 * Set the descriptive content for a given page.
	 *  
	 * @param page category
	 * @param header subject header. This will be shown as a html header for the 
	 * content paragraph
	 * @param description a description about the items shown between the header and the list 
	 * @param contents content paragraphs
	 */
	void setPageContent(String page, String header, String description, List<String> contents) {
		int index = tabs.indexOfTab(page);
		if (index != -1) {
			Component comp = tabs.getComponent(index);
			if (comp instanceof Page) {
				((Page) comp).setContent(header, description, contents);
			}
		}
	}
	
	/**
	 * Get the window component.
	 * 
	 * @return travel log window
	 */
	Component getWindow() {
		return window;
	}
	
	/**
	 * Listener for tab changes. Requests the page to update its index when it's
	 * selected.
	 */
	private class TabChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			Component selected = tabs.getSelectedComponent(); 
			if (selected instanceof Page) {
				((Page) selected).update();
			}
		}
	}
	
	/**
	 * A page on the window.
	 */
	private static class Page extends JComponent implements HyperlinkListener {
		/** Html area for the subjects */
		private final JEditorPane indexArea;
		/** The html area */
		private final JEditorPane contentArea;
		/** Scrolling component of the index area */
		private final JScrollPane indexScrollPane;
		/** Scrolling component of the content html area */
		private final JScrollPane contentScrollPane;
		
		/** Query that is used to update the index area */
		private ProgressStatusQuery indexQuery;
		/** Additional data for the index updating query */
		private String indexQueryData;
		
		/** Query that is used to update the content area */
		private ProgressStatusQuery contentQuery;
		/** Additional data for the content updating query */
		private String contentQueryData;
		
		/**
		 * Create a new page.
		 */
		public Page() {
			this.setLayout(new SBoxLayout(SBoxLayout.VERTICAL));
			JComponent panels = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
			add(panels, SBoxLayout.constraint(SLayout.EXPAND_X, 
					SLayout.EXPAND_Y));
			
			indexArea = new JEditorPane();
			indexArea.setContentType("text/html");
			indexArea.setEditable(false);
			indexArea.setFocusable(false);
			indexArea.addHyperlinkListener(this);
			
			indexScrollPane = new JScrollPane(indexArea);
			// Fixed width
			indexScrollPane.setMaximumSize(new Dimension(INDEX_WIDTH, Integer.MAX_VALUE));
			indexScrollPane.setMinimumSize(new Dimension(INDEX_WIDTH, 0));
			panels.add(indexScrollPane, SBoxLayout.constraint(SLayout.EXPAND_Y));
			
			contentArea = new JEditorPane();
			contentArea.setContentType("text/html");
			contentArea.setEditable(false);
			contentArea.setFocusable(false);
			// Does not need a listener. There should be no links
			
			contentScrollPane = new JScrollPane(contentArea);
			panels.add(contentScrollPane, SBoxLayout.constraint(SLayout.EXPAND_X,
					SLayout.EXPAND_Y));
			
			// A button for reloading the page contents
			JButton refresh = new JButton("Update");
			refresh.setFocusable(false);
			refresh.setAlignmentX(Component.RIGHT_ALIGNMENT);
			refresh.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(SBoxLayout.COMMON_PADDING,
					SBoxLayout.COMMON_PADDING, SBoxLayout.COMMON_PADDING, 
					SBoxLayout.COMMON_PADDING), refresh.getBorder()));
			refresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					update();
				}
			});
			add(refresh);
		}
		
		/**
		 * Update the page from the latest data from the server.
		 */
		public void update() {
			if (indexQuery != null) {
				indexQuery.fire(indexQueryData);
			}
			if (contentQuery != null && (contentQueryData != null)) {
				contentQuery.fire(contentQueryData);
			}
		}
		
		/**
		 * Set the query information for updating the the index.
		 * 
		 * @param query
		 * @param queryData additional data for the query
		 */
		public void setIndexQuery(ProgressStatusQuery query, String queryData) {
			this.indexQuery = query;
			this.indexQueryData = queryData;
		}
		
		/**
		 * Set the subject index.
		 * 
		 * @param subjects list of subjects available on this page
		 * @param onClick query to be used for requesting data for a subject.
		 *	Subject name will be used as the query parameter
		 */
		public void setIndex(List<String> subjects, ProgressStatusQuery onClick) {
			StringBuilder text = new StringBuilder("<html>");
			for (String elem : subjects) {
				// Make the elements clickable only if we have a handler for the
				// clicks
				if (onClick != null) {
					text.append("<a href=\"");
					text.append(elem);
					text.append("\">");
					text.append(elem);
					text.append("</a>");
				} else {
					text.append(elem);
				}
				text.append("<p>");
			}
			text.append("</html>");
			indexArea.setText(text.toString());
			contentQuery = onClick;

			/*
			 * Try to keep in the same position as before updating. This needs
			 * to be pushed to the even queue, because otherwise the scroll
			 * event triggered by changing the text would run after this.
			 */
			final JScrollBar bar = indexScrollPane.getVerticalScrollBar();
			final int position = bar.getValue();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					int newValue = Math.min(position, bar.getMaximum());
					indexScrollPane.getVerticalScrollBar().setValue(newValue);
				}
			});
		}
		
		/**
		 * Set the page contents. Each of the content strings is shown as its
		 * own paragraph.
		 * 
		 * @param header page header
		 * @param description description of the quest
		 * @param contents content paragraphs
		 */
		void setContent(String header, String description, List<String> contents) {
			StringBuilder text = new StringBuilder("<html><style type=\"text/css\">ul, li {margin-left:10px}</style>");

			// header
			if (header != null) {
				text.append("<h2>");
				text.append(header);
				text.append("</h2>");
			}

			// description
			if (description != null) {
				text.append("<i>");
				text.append(description);
				text.append("</i>");
			}

			// details
			if (!contents.isEmpty()) {
				text.append("<ul>");
				for (String elem : contents) {
					text.append("<li>");
					text.append(elem);
					text.append("</li>");
				}
				text.append("</ul>");
			}
			text.append("</html>");
			contentArea.setText(text.toString());

			/*
			 * Scroll to top. This needs to be pushed to the even queue, because
			 * otherwise the scroll event triggered by changing the text would run
			 * after this.
			 */
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					contentScrollPane.getVerticalScrollBar().setValue(0);
				}
			});	
		}

		public void hyperlinkUpdate(HyperlinkEvent event) {
			if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				/*
				 * It would be more correct to read the parameter from the link
				 * target, but swing does not give access to that when it fails
				 * to parse it as an URL.
				 */
				contentQueryData = event.getDescription();
				if (contentQuery != null) {
					contentQuery.fire(contentQueryData);
				}
			}
		}
	}
}
