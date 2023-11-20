// Author: Bora
package view;

import data_access.ApplyDataAccessObject;
import entity.CommonUniversityFactory;
import entity.UniversityFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.apply.ApplyController;
import interface_adapter.apply.ApplyPresenter;
import interface_adapter.apply.ApplyState;
import interface_adapter.apply.ApplyViewModel;
import interface_adapter.search.SearchState;
import use_case.apply.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class Applyview extends JPanel implements ActionListener, PropertyChangeListener {
    public final String viewName = "Submit form";

    private final ApplyViewModel applyViewModel;
    private final JTextField actInputField = new JTextField(15);
    private final JTextField satInputField = new JTextField(15);

    private final ApplyController applyController;

    private final JButton submit;


    public Applyview(ApplyController controller, ApplyViewModel applyViewModel) {

        this.applyController = controller;
        this.applyViewModel = applyViewModel;
        applyViewModel.addPropertyChangeListener(this);

        JLabel title = new JLabel(ApplyViewModel.TITLE_LABEL);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        LabelTextPanel actInfo = new LabelTextPanel(
                new JLabel(ApplyViewModel.ACT_LABEL), actInputField);
        LabelTextPanel satInfo = new LabelTextPanel(
                new JLabel(ApplyViewModel.SAT_LABEL), satInputField);


        JPanel buttons = new JPanel();
        submit = new JButton(ApplyViewModel.SUBMIT_BUTTON_LABEL);
        buttons.add(submit);
        submit.addActionListener(new ActionListener() {
                                     @Override
                                     public void actionPerformed(ActionEvent evt) {
                                         if (evt.getSource().equals(submit)) {
                                             System.out.println("submit Button pressed");
                                             ApplyState applyState = applyViewModel.getState();

                                             applyController.execute(applyState.getSat(),applyState.getAct());
                                         }
                                     }
                                 });
        actInputField.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        ApplyState currentState = applyViewModel.getState();
                        String text = actInputField.getText() + e.getKeyChar();
                        currentState.setAct(text);
                        applyViewModel.setState(currentState);
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                }
        );
        satInputField.addKeyListener(
                new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        ApplyState currentState = applyViewModel.getState();
                        String text = satInputField.getText() + e.getKeyChar();
                        currentState.setSat(text);
                        applyViewModel.setState(currentState);
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                }
        );



        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.add(title);
        this.add(actInfo);
        this.add(satInfo);
        this.add(buttons);
    }

    /**
     * React to a button click that results in evt.
     */
    @Override
    public void actionPerformed(ActionEvent evt) {
        JOptionPane.showConfirmDialog(this, "Apply not implemented yet.");
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String y = evt.getPropertyName();
        if (y.equals("state")) {
            // Results View Not implemented yet, will be implemented soon.
            ApplyState state = (ApplyState) evt.getNewValue();
            JOptionPane.showMessageDialog(this, state.getUni());
            System.out.println(state.getUni().getSchoolName());
        } else {
            ApplyState state = (ApplyState) evt.getNewValue();
            JOptionPane.showMessageDialog(this, state.getUniversityError());
            state.setUniversityError(null);
        }
    }
    public static void main(String[] args) {
        JFrame application = new JFrame("ApplyView Test");
        application.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ViewManagerModel viewManagerModel = new ViewManagerModel();
        ApplyViewModel applyViewModel = new ApplyViewModel();
        ApplyOutputBoundary applyPresenter = new ApplyPresenter(applyViewModel,viewManagerModel);
        //ApplyInputData applyInputData = new ApplyInputData();
        ApplyDataAccessInterface applyDataAccessInterface = new ApplyDataAccessObject();
        UniversityFactory universityFactory = new CommonUniversityFactory();
        ApplyInputBoundary applyUseCaseInteractor = new ApplyInteractor(applyDataAccessInterface,applyPresenter,universityFactory);
        ApplyController applyController = new ApplyController( applyUseCaseInteractor);
        Applyview applyView = new Applyview(applyController, applyViewModel);

        JPanel testPanel = new JPanel();
        testPanel.add(applyView);

        application.add(testPanel);
        application.pack();
        application.setVisible(true);
    }

}
