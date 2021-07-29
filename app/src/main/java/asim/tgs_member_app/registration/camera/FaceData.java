package asim.tgs_member_app.registration.camera;

import android.graphics.PointF;

public class FaceData
{
    private PointF leftEyePosition,rightEyePosition,noseEyePosition
            ,leftMouthPosition,rightMouthPosition,
            bottomMouthPosition,leftEarPosition,rightEarPosition,
            leftEarTipPosition,rightEarTipPosition,rightCheekPosition,
            leftCheekPosition;
    private PointF facePosition;
    private float width,height;
    private boolean leftEyeOpen,rightEyeOpen,faceSmiling;

    public FaceData() {
        leftEyeOpen = true;
        rightEyeOpen = true;
        faceSmiling = false;
    }

    public boolean isFaceSmiling() {
        return faceSmiling;
    }

    public void setFaceSmiling(boolean faceSmiling) {
        this.faceSmiling = faceSmiling;
    }

    public boolean isLeftEyeOpen() {
        return leftEyeOpen;
    }

    public void setLeftEyeOpen(boolean leftEyeOpen) {
        this.leftEyeOpen = leftEyeOpen;
    }

    public boolean isRightEyeOpen() {
        return rightEyeOpen;
    }

    public void setRightEyeOpen(boolean rightEyeOpen) {
        this.rightEyeOpen = rightEyeOpen;
    }

    public PointF getFacePosition() {
        return facePosition;
    }

    public void setFacePosition(PointF facePosition) {
        this.facePosition = facePosition;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public PointF getLeftEyePosition() {
        return leftEyePosition;
    }

    public PointF getLeftEarTipPosition() {
        return leftEarTipPosition;
    }

    public void setLeftEarTipPosition(PointF leftEarTipPosition) {
        this.leftEarTipPosition = leftEarTipPosition;
    }

    public PointF getRightEarTipPosition() {
        return rightEarTipPosition;
    }

    public void setRightEarTipPosition(PointF rightEarTipPosition) {
        this.rightEarTipPosition = rightEarTipPosition;
    }

    public void setLeftEyePosition(PointF leftEyePosition) {
        this.leftEyePosition = leftEyePosition;
    }

    public PointF getRightEyePosition() {
        return rightEyePosition;
    }

    public void setRightEyePosition(PointF rightEyePosition) {
        this.rightEyePosition = rightEyePosition;
    }

    public PointF getNoseEyePosition() {
        return noseEyePosition;
    }

    public void setNoseEyePosition(PointF noseEyePosition) {
        this.noseEyePosition = noseEyePosition;
    }

    public PointF getLeftMouthPosition() {
        return leftMouthPosition;
    }

    public void setLeftMouthPosition(PointF leftMouthPosition) {
        this.leftMouthPosition = leftMouthPosition;
    }

    public PointF getRightMouthPosition() {
        return rightMouthPosition;
    }

    public void setRightMouthPosition(PointF rightMouthPosition) {
        this.rightMouthPosition = rightMouthPosition;
    }

    public PointF getBottomMouthPosition() {
        return bottomMouthPosition;
    }

    public void setBottomMouthPosition(PointF bottomMouthPosition) {
        this.bottomMouthPosition = bottomMouthPosition;
    }

    public PointF getLeftEarPosition() {
        return leftEarPosition;
    }

    public void setLeftEarPosition(PointF leftEarPosition) {
        this.leftEarPosition = leftEarPosition;
    }

    public PointF getRightEarPosition() {
        return rightEarPosition;
    }

    public void setRightEarPosition(PointF rightEarPosition) {
        this.rightEarPosition = rightEarPosition;
    }

    public PointF getRightCheekPosition() {
        return rightCheekPosition;
    }

    public void setRightCheekPosition(PointF rightCheekPosition) {
        this.rightCheekPosition = rightCheekPosition;
    }

    public PointF getLeftCheekPosition() {
        return leftCheekPosition;
    }

    public void setLeftCheekPosition(PointF leftCheekPosition) {
        this.leftCheekPosition = leftCheekPosition;
    }
}